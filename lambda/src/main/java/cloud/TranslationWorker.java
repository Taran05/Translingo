package cloud;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import software.amazon.awssdk.services.s3.S3Client;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;
import com.amazonaws.services.lambda.runtime.Context;

public class TranslationWorker implements RequestHandler<SQSEvent, Void> {
    private final S3Client s3Client;
    private final SqsClient sqsClient;
    private final TranslateClient translateClient;
    private static final String INPUT_BUCKET_NAME = "b00945917-document";
    private final String OUTPUT_BUCKET_NAME = "b00945917-translate";
    private String sqsQueueUrl = "https://sqs.us-east-1.amazonaws.com/111143347609/b00945917-queue";

    public TranslationWorker() {
        String region = "us-east-1";
        String accessKey = "";
        String secretKey = "";

        // Create the AWS credentials using the values from the properties file
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // Set the AWS region
        Region awsRegion = Region.of(region);

        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(awsRegion)
                .build();

        this.sqsClient = SqsClient.builder()
                .region(awsRegion)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        this.translateClient = TranslateClient.builder()
                .region(awsRegion)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Override
    public Void handleRequest( SQSEvent event, Context context) {
        System.out.println("Welcome");
        for(SQSEvent.SQSMessage msg : event.getRecords()){
            processMessages(msg);
        }
        return null;
    }

    public void processMessages(SQSEvent.SQSMessage message) {
        try {
                System.out.println("processing");
                String[] messageParts = message.getBody().split(",");
                String documentId = messageParts[0];
                String sourceLanguage = messageParts[1];
                String targetLanguage = messageParts[2];
                System.out.println(messageParts[0]);
                System.out.println(messageParts[1]);
                System.out.println(messageParts[2]);
                // Retrieve the document from Amazon S3
                InputStream documentStream = s3Client.getObject(GetObjectRequest.builder()
                        .bucket(INPUT_BUCKET_NAME)
                        .key(documentId)
                        .build());
                System.out.println(documentStream);
                // Perform the translation using Amazon Translate
                String translatedText = performTranslation(documentStream, sourceLanguage, targetLanguage);
                // Store the translated document back in Amazon S3
                System.out.println(translatedText);
                storeTranslatedDocument(documentId, translatedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String performTranslation(InputStream documentStream, String sourceLanguage, String targetLanguage) throws IOException {
        System.out.println("translating");
        try {
            // Read the document content from the input stream
            byte[] documentBytes = documentStream.readAllBytes();
            String documentContent = new String(documentBytes, StandardCharsets.UTF_8);

            // Perform the translation using Amazon Translate
            TranslateTextRequest translateRequest = TranslateTextRequest.builder()
                    .sourceLanguageCode(sourceLanguage)
                    .targetLanguageCode(targetLanguage) // Replace with your desired target language code
                    .text(documentContent)
                    .build();

            TranslateTextResponse translateResponse = translateClient.translateText(translateRequest);

            return translateResponse.translatedText();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            throw e;
        }
    }

    private void storeTranslatedDocument(String documentId, String translatedText) {
        System.out.println("storing");
        // Generate a new key for the translated document
        String translatedDocumentKey = documentId;

        // Store the translated document in Amazon S3
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(OUTPUT_BUCKET_NAME)
                .key(translatedDocumentKey)
                .build(), RequestBody.fromString(translatedText));
    }
}