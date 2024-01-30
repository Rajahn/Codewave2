package edu.vt.ranhuo.codewavecommon.utils;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.nio.file.Paths;
import java.time.Duration;

public class S3Utils {

    private static final String BUCKET_NAME = "codewaveaudio";
    private static final String ACCESS_KEY = "AKIAW5FQIIYG7QVH5DOK"; // 替换为你的AK
    private static final String SECRET_KEY = ""; // 替换为你的SK
    private static final Region REGION = Region.US_EAST_1;
    private S3Client s3Client;

    private S3Presigner s3Presigner;

    public S3Utils() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
        s3Client = S3Client.builder()
                .region(REGION)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        s3Presigner = S3Presigner.builder()
                .region(REGION)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public String uploadFile(String filePath, String keyName) {
        // 检查对象是否已经存在
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(BUCKET_NAME).key(keyName).build());
            throw new RuntimeException("Object with key " + keyName + " already exists in the bucket!");
        } catch (NoSuchKeyException e) {
            // 对象不存在，可以上传
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(keyName)
                            .build(),
                    RequestBody.fromFile(Paths.get(filePath)));

            // 生成预签名URL
            return generatePresignedUrl(keyName, Duration.ofHours(1)); // 有效期为1小时
        }
    }

    private String generatePresignedUrl(String keyName, Duration duration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(keyName)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(b -> b.getObjectRequest(getObjectRequest).signatureDuration(duration));
        return presignedGetObjectRequest.url().toString();
    }

    public void downloadFile(String keyName, String outputPath) {
        s3Client.getObject(GetObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(keyName)
                        .build(),
                ResponseTransformer.toFile(Paths.get(outputPath)));
    }

    public void deleteObject(String keyName) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(keyName)
                    .build());
            System.out.println("Object with key '" + keyName + "' has been deleted.");
        } catch (Exception e) {
            System.out.println("Error deleting object with key '" + keyName + "': " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        S3Utils s3Utils = new S3Utils();

        //s3Utils.downloadFile("your_desired_object_id.mp3", "path_to_save_downloaded_file.mp3");
        //s3Utils.deleteObject("your_desired_object_id.mp3");

//        try {
//            String url = s3Utils.uploadFile("src/main/resources/tts/final_output.mp3", "your_desired_object_id.mp3");
//            System.out.println("Uploaded object URL: " + url);
//        } catch (RuntimeException e) {
//            System.out.println(e.getMessage());
//        }
        String presignedUrl = s3Utils.uploadFile("src/main/resources/tts/final_output.mp3", "your_desired_object_id.mp3");
        System.out.println("Presigned URL: " + presignedUrl);
    }
}
