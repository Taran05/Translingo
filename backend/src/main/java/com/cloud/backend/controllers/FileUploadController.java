package com.cloud.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private final S3Client s3Client;
    private static final String BUCKET_NAME = "b00945917-document";

    public FileUploadController() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream("/aws-config.properties")) {
            properties.load(inputStream);
        }

        // Read the values from the properties file
        String region = properties.getProperty("AWS_REGION");
        String accessKey = properties.getProperty("AWS_ACCESS_KEY_ID");
        String secretKey = properties.getProperty("AWS_SECRET_ACCESS_KEY");

        // Create the AWS credentials using the values from the properties file
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // Set the AWS region
        Region awsRegion = Region.of(region);

        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(awsRegion) // Replace with your desired region
                .build();
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            // Upload the file to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(fileName)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return ResponseEntity.ok("File uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

}
