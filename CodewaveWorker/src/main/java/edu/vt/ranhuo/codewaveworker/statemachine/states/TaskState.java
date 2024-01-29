package edu.vt.ranhuo.codewaveworker.statemachine;

public interface TaskState {
    void handle(TaskContext context);
}
