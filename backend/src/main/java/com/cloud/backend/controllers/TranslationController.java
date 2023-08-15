package com.cloud.backend.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api")
public class TranslationController {

    private final SqsClient sqsClient;
    public TranslationController() throws IOException {
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
        this.sqsClient = SqsClient.builder()
                .region(awsRegion)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }


    @Value("${aws.sqs.queue.url}")
    private String sqsQueueUrl;

    @PostMapping("/translate/{documentId}")
    public ResponseEntity<String> initiateTranslation(@PathVariable String documentId, @RequestBody Map<String, String> requestBody) {
        String sourceLanguage = requestBody.get("sourceLanguage");
        String targetLanguage = requestBody.get("targetLanguage");
        try {
            // Enqueue the translation job details in the SQS queue
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(sqsQueueUrl)
                    .messageBody(String.format("%s,%s,%s", documentId, sourceLanguage, targetLanguage))
                    .build();
            sqsClient.sendMessage(sendMessageRequest);
            return ResponseEntity.ok("Translation initiated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Translation initiation failed: " + e.getMessage());
        }
    }


}
