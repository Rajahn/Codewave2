package edu.vt.ranhuo.codewaveworker.statemachine;

import edu.vt.ranhuo.asyncslave.context.ISlave;
import edu.vt.ranhuo.codewavecommon.common.ErrorCode;
import edu.vt.ranhuo.codewavecommon.model.dto.TaskUpdate;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;
import edu.vt.ranhuo.codewaveworker.utils.AsyncUtil;

public interface TaskCallback {

    ISlave slave = AsyncUtil.createSlave();
    Task onPollTask();

    TaskUpdate onProcessingTask(Task task);
    void onPostTaskUpdate(Task task,TaskUpdate taskUpdate);

    void onPostError(Exception e);
}
