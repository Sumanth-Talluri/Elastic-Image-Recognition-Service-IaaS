package com.cloudcomputing.imagerecognizer.webtier.aws.impl;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.cloudcomputing.imagerecognizer.webtier.aws.AwsConfig;
import com.cloudcomputing.imagerecognizer.webtier.aws.SQSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SQSServiceImpl implements SQSService {

    @Autowired
    private AwsConfig awsConfig;

    @Override
    public void pushToQueue(String messageBody, String queueName, Integer delay) throws Exception {
        log.debug("pushing the message to queue ");
        try {
            String queueUrl = awsConfig.getAmazonSQSClient().getQueueUrl(queueName).getQueueUrl();
            SendMessageRequest sendMessageRequest = new SendMessageRequest().withQueueUrl(queueUrl)
                    .withMessageBody(messageBody)
                    .withDelaySeconds(delay);
            awsConfig.getAmazonSQSClient().sendMessage(sendMessageRequest);
            log.info("successfully pushed the message: {} to queue: {}", messageBody, queueName);

        } catch (Exception e) {
            log.error("Unexpected error while pushing message: {} to queue: {} : ", messageBody, queueName);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void deleteMessage(Message message, String queue) {
        log.debug("Deleting message from the sqs queue: {}", queue);
        String queueUrl = awsConfig.getAmazonSQSClient().getQueueUrl(queue).getQueueUrl();
        DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest(queueUrl, message.getReceiptHandle());
        awsConfig.getAmazonSQSClient().deleteMessage(deleteMessageRequest);
    }

    @Override
    public Message consumeFromQueue(String queue, Integer waitTime, Integer visibilityTimeout) {
        log.debug("Receiving the message from the queue.");
        String queueUrl = awsConfig.getAmazonSQSClient().getQueueUrl(queue).getQueueUrl();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest().withQueueUrl(queueUrl)
                .withMaxNumberOfMessages(1)
                .withWaitTimeSeconds(waitTime)
                .withVisibilityTimeout(visibilityTimeout);
        List<Message> messages = awsConfig.getAmazonSQSClient().receiveMessage(receiveMessageRequest).getMessages();
        return messages.isEmpty() ? null : messages.get(0);
    }
}
