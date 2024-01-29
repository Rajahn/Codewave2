package edu.vt.ranhuo.codewaveworker;

import edu.vt.ranhuo.codewaveworker.service.TextToSoundTaskService;
import edu.vt.ranhuo.codewaveworker.statemachine.TaskContext;
import edu.vt.ranhuo.codewaveworker.statemachine.states.TaskStates;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = {"edu.vt.ranhuo.codewaveworker.config","edu.vt.ranhuo.codewaveworker.service"})
public class CodewaveWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodewaveWorkerApplication.class, args);
    }

}
