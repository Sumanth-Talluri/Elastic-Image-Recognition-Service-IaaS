package com.cloudcomputing.imagerecognizer.webtier.service.impl;

import com.cloudcomputing.imagerecognizer.webtier.aws.S3Service;
import com.cloudcomputing.imagerecognizer.webtier.aws.SQSService;
import com.cloudcomputing.imagerecognizer.webtier.service.RecognizerService;
import com.cloudcomputing.imagerecognizer.webtier.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class RecognizerServiceImpl implements RecognizerService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private SQSService sqsService;

    @Value("${aws.sqs.request.queue.name}")
    private String requestQueueName;

    @Value("${aws.sqs.request.queue.delay}")
    private String delay;

    @Override
    public void uploadImage(MultipartFile multipartFile, String fileName) throws Exception {
        File uploadedFile = FileUtils.convertToFile(multipartFile);
        s3Service.uploadFile(uploadedFile, fileName);
        sqsService.pushToQueue(fileName, requestQueueName, Integer.valueOf(delay));
    }
}
