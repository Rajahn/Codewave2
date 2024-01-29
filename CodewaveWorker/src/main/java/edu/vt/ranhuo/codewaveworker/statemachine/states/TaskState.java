package edu.vt.ranhuo.codewaveworker.statemachine.states;

import edu.vt.ranhuo.codewaveworker.statemachine.TaskContext;

public interface TaskState {
    void handle(TaskContext context);
}
