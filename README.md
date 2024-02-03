
---

# Codewave2 分布式任务执行框架

为了运行自己训练的文本生成音频（Text-to-Speech）深度学习模型时，能够同时利用多机GPU资源，开发出本项目，在后续迭代中将其较为通用的能力重构成一个分布式任务执行框架。目前本项目的核心能力支持**任务分片，心跳检测，执行/调度节点宕机恢复，执行/调度节点水平扩展，任务超时/异常重试，根据任务优先级调度，执行进度可视化，滚动分表**等。

本项目整体分为`Server`和`Worker`两端，均依靠`Codewave-Async`模块提供可靠的任务治理能力。

![Codewave2 部署架构图](https://github.com/Rajahn/Codewave2/assets/39303094/2c011afa-2b8e-40ba-99e8-41f694588b79)

## 以下流程描述以文本生成音频任务为例

### Server的职责是

1. 接收用户上传的文本，创建一个作业存入Job表。
2. 执行作业分片，对于文本生成音频任务来说就是把长文本拆分成段落，存入Task表，初始化所有task状态为`ready`，在Redis中保存当前job分出的待执行task数量。
3. 运行任务提交定时任务，根据当前健康运行的Worker实例数，以及Codewave-Async就绪队列中排队的任务数，将若干task提交入就绪队列并更新状态。
4. 运行任务结束定时任务，从Codewave-Async结果队列中取出处理完成的task，根据业务需要判断task是否需要重试，不需要即可更新task状态为`finished`，同时减少Redis中当前task对应job的待执行任务数量；当待执行任务数量减少为0时，汇总所有task执行结果，更新入Job表中，当前Job整体执行完成。

### Worker的职责是
![worker状态机](https://github.com/Rajahn/Codewave2/assets/39303094/5fb7555d-46f9-4b55-9d77-896c7b3a570e)

1. 运行一个任务处理状态机，分为`Pending`，`Polling`，`Working`，`Reporting`，`Error`状态。从pending开始，随机等待后进入polling，尝试从就绪队列拉取任务，如果拉取为空就回到pending；拉取成功则执行业务逻辑，同时检测任务执行是否超时，如果超时或出现其他异常则进入error状态，否则进入reporting状态，此两者均会向结果队列提交任务，之后回到pending。
2. 可以启用多个状态机线程，执行不同的业务逻辑。

## Codewave-Async分布式任务队列模块 [link](https://github.com/Rajahn/CodewaveAsync)

Async模块分为`master`和`slave`，目前需要本地打包成jar包引入。
Async利用redis实现了任务就绪队列，执行队列，结果队列，以及心跳检测机制。

![Async-Master-Slave](https://github.com/Rajahn/Codewave2/assets/39303094/d79fe180-9bd5-4594-8de2-5c9e4692c1ca)


### 1. 就绪队列

- 就绪/重试队列是两个不同的队列，均用于让slave获取任务，slave只会在就绪队列为空时尝试从重试队列中获取任务，这两个队列中的任务均由master提交，所有节点共享。
- 该队列采用`zset`结构，配合优先级字段实现任务按优先级调度。

### 2. 执行队列

- 每个master/slave节点都有独立的执行队列，存储正在执行中的任务信息，用于宕机后恢复。

### 3. 结果队列

- 用于存放已经结束的任务，由worker提交，master进行消费。

### master职责

- master负责将任务提交到就绪/重试队列。
- master从结果队列左侧消费已完成的任务。
- master每隔一段时间向心跳队列发送心跳时间戳。

### slave职责

- slave负责从就绪/重试队列获取任务。
- slave具体业务执行完毕后，向结果队列提交任务。
- slave每隔一段时间向心跳队列发送心跳时间戳。

## 心跳检测机制实现 
![Async-heartbeat](https://github.com/Rajahn/Codewave2/assets/39303094/58b059d5-ed82-4bd1-bb88-39918cf4e6dd)

- master/slave在启动时便会不断向心跳队列发送心跳，结构为hash，key为节点信息，value为当前时间戳。
- master/slave 启动时抢占一个分布式锁，确定一个节点为leader。
- 具有leader身份的节点负责每隔一段时间获取心跳队列中的数据，当发现某个节点的hash value时间戳与当前时间差距大于阈值，即认定此节点宕机，执行宕机处理。

## 宕机处理

- leader节点通过宕机节点的hashKey判断是master节点还是slave节点。
- 对于宕机的master节点，将其执行队列中的任务放入结果队列左侧。
- 对于宕机的slave节点，将其执行队列中的任务放入就绪队列。
- 以上操作完成后删除执行队列的hashkey，并删除心跳队列的hashkey。
- 如果具有leader身份的节点宕机，分布式锁过期释放，其他节点会重新开始抢占成为leader。

## 优先级调度

- 每个task在加入就绪队列时，以其orderTime作为zset的score，orderTime是一个长整型。
- 在任务创建时，orderTime = 当前时间 - priority，priority代表优先priority秒进行调度。
- 当任务重试时，orderTime = 当前时间 + delay 降低优先级。
- 具体的priority可以由业务方实现。

## 滚动分表

- 由于采用job拆分成task的执行模式，Task表可能数据量过大造成性能下降，因此设计分表机制。
- 以`schedule_pos`表实现对任务表进行滚动分表，其中关键字段`begin_pos`表示热任务记录从几号表开始，`end_pos`表示热任务记录在几号表结束，初始都为1，表示只有一张表。
- 当任务治理定时任务发现1号表数据量达到分表阈值，触发分表，创建2号表。
- 此时Server提交任务到就绪队列时查询1号表，即begin_pos为1，当新增的任务将直接存入2号表，即end_pos为2。
- Server持续从1号表提交任务，当1号表所有任务都已被执行完毕，更新begin_pos为2，此后的任务提交与新增都使用2号表。

## 执行进度可视化

- 由于在Redis中存储了Job待完成的任务数量，因此可以利用此数值的更新代表作业执行进度，利用SSE推送给前端。

## 设计演进过程

本项目的设计与实现来自VT CS5704课程，经过后续多轮重构。调研常见分布式任务组件，我们场景的主要需求在于分布式的任务执行，且需要为多阶段任务，任务重试，优先级调度功能做预留设计，发现如xxl-job等组件主要定位在执行定时任务，消息队列主要在于消息流转，其主要功能并不是我们想要的，且引入较重，购买服务增加成本。我们场景的主要制约在于：

1. 租不起GPU服务器跑项目，只能利用几台个人pc作为worker节点。
2. 我们的通信使用内网穿透工具ngrok，经常掉线断联。
3. yash同学的深度学习模型过于复杂，且依赖其他在本地运行的ai音频增强底座服务，多次尝试打

包docker失败，遂只能本地运行，无法构建集群或使用运维工具。

**开发阶段1**：任务到达后直接拆分存入数据库，只支持使用一个worker，ip地址写死在配置文件，使用异步线程任务向worker推送，实现了基本功能。缺点：每来一个任务就要启用一个线程处理，即便自定义了线程池，请求处理能力仍十分有限；只能使用一个worker，效率非常低下，且worker阻塞/任务失败后任务信息丢失。
                [codewave1实现](https://github.com/Rajahn/CodewaveService)

**开发阶段1.5**：尝试在worker侧用docker-compose实现一个简单的 [集群](https://github.com/Rajahn/TTS_XunFei_Service_Deployment_With_Docker_Compose)，采用轮询分配请求，提升处理能力，已验证demo；但经yash同学多次努力后其模型打包docker失败，此方案作废。

**开发阶段2**：使用Redis做消息队列，任务到达后先放入redis缓存（有可能丢失），然后逐步写入数据库，尝试使用Nacos等服务注册中心实现服务注册，权衡后放弃；把向worker推送改为worker主动拉取任务，至此支持多个worker同时工作，效率有所提升。缺点：任务失败/worker节点宕机后任务丢失。

**开发阶段3**：将任务治理模块拆分成codewave-async模块，基于redis实现了可靠的分布式任务队列，实现心跳机制，因此支持节点宕机后执行中任务转移，任务超时重试，任务按优先级调度，任务分片等功能。

**开发阶段4**：鉴于分片后任务数量可能较多，冗余设计了分表功能，避免单表数据量过多性能下降。

---
