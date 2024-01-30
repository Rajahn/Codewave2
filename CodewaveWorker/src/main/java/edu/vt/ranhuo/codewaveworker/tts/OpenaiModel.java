package edu.vt.ranhuo.codewaveworker.tts;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class OpenaiModel{

    public static String generateAudio(String taskName,String text) throws IOException {
        String path = "src/main/resources/tts/" + taskName+".mp3";
        String result = textToSpeech(text);
//        byte[] audioByte = Base64.getDecoder().decode(result);
//        try (OutputStream outputStream = new FileOutputStream(path)) {
//            outputStream.write(audioByte);
//        }
        return result;
    }

    private static final String OPENAI_URL = "https://api.openai.com/v1/audio/speech";
    private static final String API_KEY = "";

    public static String textToSpeech(String text) {
        try {
            URL url = new URL("https://api.openai.com/v1/audio/speech");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{\"model\": \"tts-1\", \"input\": \"" + text + "\", \"voice\": \"onyx\"}";

            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try(BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                ByteArrayOutputStream buf = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bis.read(buffer, 0, 1024)) != -1) {
                    buf.write(buffer, 0, bytesRead);
                }
                byte[] mp3Data = buf.toByteArray();
                return Base64.getEncoder().encodeToString(mp3Data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
