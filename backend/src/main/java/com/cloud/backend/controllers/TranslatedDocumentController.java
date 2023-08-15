package com.cloud.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class TranslatedDocumentController {

    private final S3Client s3Client;
    private static final String BUCKET_NAME = "b00945917-translate";

    public TranslatedDocumentController() throws IOException {
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

    @GetMapping("/translated-documents")
    public ResponseEntity<List<String>> getUploadedDocuments() {
        try {
            // List all objects (documents) in the bucket
            ListObjectsV2Response response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(BUCKET_NAME)
                    .build());

            // Extract the object names and return as a list
            List<String> documents = response.contents()
                    .stream()
                    .map(S3Object::key)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
