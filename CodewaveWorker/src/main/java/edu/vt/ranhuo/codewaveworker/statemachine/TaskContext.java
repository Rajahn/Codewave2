package edu.vt.ranhuo.codewaveworker.statemachine;

import edu.vt.ranhuo.codewavecommon.model.dto.TaskUpdate;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;
import edu.vt.ranhuo.codewaveworker.statemachine.states.TaskState;
import lombok.Data;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
@Data
public class TaskContext {
    private TaskCallback callback;

    private Task task;
    private TaskUpdate taskUpdate;
    private Exception exception;

    public TaskContext(TaskCallback callback) {
        this.callback = callback;
    }

    public void setState(TaskState state) {
        state.handle(this); // 立即处理新状态
    }

    public void handleRandomDelay() {
        int delay = ThreadLocalRandom.current().nextInt(5, 10); // 随机延迟时间（1到10秒）
        try {
            TimeUnit.SECONDS.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public TaskCallback getCallback() {
        return callback;
    }

    public void clean(){
        task = null;
        taskUpdate = null;
        exception = null;
    }
}
