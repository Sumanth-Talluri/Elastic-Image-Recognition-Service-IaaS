package com.cloudcomputing.imagerecognizer.webtier.aws;

import com.amazonaws.services.sqs.model.Message;

public interface SQSService {

    void pushToQueue(String messageBody, String queueName, Integer delay) throws Exception;

    void deleteMessage(Message message, String queue);

    Message consumeFromQueue(String queue, Integer waitTime, Integer visibilityTimeout);
}
