package edu.vt.ranhuo.codewaveworker.runner;

import edu.vt.ranhuo.codewaveworker.service.TextToSoundTaskService;
import edu.vt.ranhuo.codewaveworker.service.TranslateTaskService;
import edu.vt.ranhuo.codewaveworker.statemachine.TaskContext;
import edu.vt.ranhuo.codewaveworker.statemachine.states.TaskStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TaskRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        TaskContext context = new TaskContext(new TextToSoundTaskService());
        context.setState(TaskStates.PENDING);
    }
}
