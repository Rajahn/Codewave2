package edu.vt.ranhuo.codewaveworker.tts;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class OpenaiModelTest {

    @Test
    public void ttsTest() throws IOException {
       String text = "Hello, my name is John. What is your name?";
       String result = OpenaiModel.generateAudio("test1",text);
        System.out.println(result);
    }

}