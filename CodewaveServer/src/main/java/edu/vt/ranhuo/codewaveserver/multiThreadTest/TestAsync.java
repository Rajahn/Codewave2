package edu.vt.ranhuo.codewaveserver.multiThreadTest;

import com.google.gson.Gson;
import edu.vt.ranhuo.asynccore.config.TaskConfig;
import edu.vt.ranhuo.asynccore.enums.QueueType;
import edu.vt.ranhuo.asyncmaster.context.IMaster;
import edu.vt.ranhuo.asyncmaster.context.Master;
import edu.vt.ranhuo.asyncslave.context.ISlave;
import edu.vt.ranhuo.asyncslave.context.Slave;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.util.Optional;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.database;
@Slf4j
public class TestAsync {

    public IMaster createMaster(){
        TaskConfig taskConfig =  TaskConfig(redissonClient());
        IMaster<String> master = new Master(taskConfig);
        return master;
    }

    public ISlave createSlave(){
        TaskConfig taskConfig =  TaskConfig(redissonClient());
        ISlave<String> slave = new Slave(taskConfig);
        return slave;
    }

    private TaskConfig TaskConfig(RedissonClient redissonClient) {
        return TaskConfig.builder()
                .prefix("codewave")
                .redissonClient(redissonClient)
                .expirationCount(3)
                .heartbeatInterval(10)
                .build();
    }

    private RedissonClient redissonClient() {
        Config config = new Config();
        config .setCodec(new JsonJacksonCodec())
                .useSingleServer()
                .setDatabase(database.ordinal())
                .setAddress("redis://" + "127.0.0.1" + ":" + "6379");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    public static void main(String[] args) {
        Task task = new Task();
        task.setId(100);
        task.setTask_input("test-task1, hahaha");
        Gson gson = new Gson();
        String taskstr = gson.toJson(task);

        TestAsync testAsync = new TestAsync();
        IMaster master = testAsync.createMaster();
        //ISlave slave = testAsync.createSlave();
        master.send(QueueType.HIGN,1,taskstr);

       // Optional task = slave.consume();
       // String taskstr = (String) task.get();
       // log.info("taskstr is {}",taskstr);
       // String res = taskstr+"hahaha";

       // slave.commit(res,taskstr);

//        String masterget = String.valueOf(master.consume());
//        log.info("masterget is {}",masterget);
//        master.commit(masterget);
    }
}
