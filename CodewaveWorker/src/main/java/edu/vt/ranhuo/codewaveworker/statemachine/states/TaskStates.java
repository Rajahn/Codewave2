package edu.vt.ranhuo.codewaveworker.statemachine.states;


import edu.vt.ranhuo.codewavecommon.model.dto.TaskUpdate;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;
import edu.vt.ranhuo.codewaveworker.statemachine.TaskContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public enum TaskStates implements TaskState {
    PENDING {
        @Override
        public void handle(TaskContext context) {
            log.info("=====task pending state start=====");
            context.handleRandomDelay(); // 随机等待
            context.setState(POLLING);
        }
    },
    POLLING {
        @Override
        public void handle(TaskContext context) {
            log.info("=====task polling state start=====");
            Task task  = context.getCallback().onPollTask(); //拉取任务
            if(task == null) {
                context.setState(PENDING);
            }else {
                context.setTask(task);
               // log.info("=====task d% polling state end=====", context.getTask().getId());
                context.setState(WORKING);
            }
        }
    },

    WORKING {
        @Override
        public void handle(TaskContext context) {
            log.info("=====task "+context.getTask().getId()+" working state start=====");
            //log.info("=====task working state start=====");
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<TaskUpdate> future = executor.submit(() ->
                    context.getCallback().onProcessingTask(context.getTask()));

            try {
                TaskUpdate taskUpdate = future.get(10, TimeUnit.SECONDS); // 等待10秒
                context.setTaskUpdate(taskUpdate);
                context.setState(REPORTING); // 只有在成功完成时才转到REPORTING状态
            } catch (TimeoutException e) {
                future.cancel(true); // 取消任务
                log.error("Task processing timed out", e);
                // 处理超时情况
                context.setException(e);
                context.setState(ERROR);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error during task processing", e);
                // 处理执行异常
                context.setException(e);
                context.setState(ERROR);
            } finally {
                executor.shutdownNow(); // 关闭ExecutorService
            }
        }
    },
    REPORTING {
        @Override
        public void handle(TaskContext context) {
            //log.info("=====task d% reporting state start=====", context.getTask().getId());
            log.info("=====task reporting state start=====");
            context.getCallback().onPostTaskUpdate(context.getTask(),context.getTaskUpdate());
            context.clean();
            context.setState(PENDING);
        }
    },

    ERROR {
        @Override
        public void handle(TaskContext context) {
            log.info("=====task error state start=====");
            context.getCallback().onPostError(context.getException());
            context.clean();
            context.setState(PENDING);
        }
    };

}


