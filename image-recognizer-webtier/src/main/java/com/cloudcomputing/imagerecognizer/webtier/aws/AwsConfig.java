package com.cloudcomputing.imagerecognizer.webtier.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AwsConfig {

    @Value("${aws.access.key}")
    private String accessKey;

    @Value("${aws.secret.key}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.sqs.response.queue.name}")
    public String responseQueue;

    @Value("${aws.sqs.request.queue.name}")
    public String requestQueue;

    private AmazonS3 s3Client;

    private AmazonSQS sqsClient;

    @PostConstruct
    public void init() {

        AWSCredentials basicCredentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(basicCredentials))
                        .withRegion(Regions.US_EAST_1)
                        .build();
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        sqsClient = AmazonSQSClientBuilder.standard()
                .withClientConfiguration(clientConfiguration.withMaxConnections(130))
                        .withCredentials(new AWSStaticCredentialsProvider(basicCredentials))
                        .withRegion(Regions.US_EAST_1)
                        .build();
    }

    public AmazonS3 getAmazonS3Client() {
        return s3Client;
    }

    public AmazonSQS getAmazonSQSClient() {
        return sqsClient;
    }





}
