package com.cloud.backend.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@RestController
@RequestMapping("/api")
public class DownloadController {

    private final S3Client s3Client;
    private static final String BUCKET_NAME = "b00945917-translate";

    public DownloadController() throws IOException {
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
                .region(awsRegion)
                .build();
    }

    @PostMapping("/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String documentId) {
        try {
            // Get the S3 object metadata
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(documentId)
                    .build();
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);
            S3Object s3Object = S3Object.builder()
                    .eTag(response.response().eTag())
                    .lastModified(response.response().lastModified())
                    .build();

            // Set the response headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + documentId);
            headers.add(HttpHeaders.CONTENT_TYPE, response.response().contentType());

            // Read the S3 object data into a byte array
            byte[] fileData = response.readAllBytes();

            // Return the file data with the appropriate headers
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
