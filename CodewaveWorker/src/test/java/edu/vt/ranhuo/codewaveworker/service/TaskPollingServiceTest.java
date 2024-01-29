package edu.vt.ranhuo.codewaveworker.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TaskPollingServiceTest {
    @Autowired
    private TextToSoundTaskService textToSoundTaskService;
    @Test
    void pollForTask() {
        //textToSoundTaskService.pollForTask();
    }
}