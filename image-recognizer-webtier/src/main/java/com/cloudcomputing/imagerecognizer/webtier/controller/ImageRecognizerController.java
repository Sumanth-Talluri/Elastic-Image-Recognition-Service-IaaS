package com.cloudcomputing.imagerecognizer.webtier.controller;

import com.amazonaws.services.sqs.model.Message;
import com.cloudcomputing.imagerecognizer.webtier.aws.SQSService;
import com.cloudcomputing.imagerecognizer.webtier.service.RecognizerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
public class ImageRecognizerController {

    Map<String, String> results = new HashMap<>();

    @Autowired
    private RecognizerService recognizerService;

    @Value("${aws.sqs.response.queue.name}")
    public String responseQueue;

    @Autowired
    private SQSService sqsService;

    @PostMapping("upload")
    public ResponseEntity<String> uploadImage(@RequestParam(value = "file") MultipartFile multipartFile) {
        String result = null;
        ResponseEntity<String> responseEntity;
        try{
            log.info("received a request to upload a file to s3: {}", multipartFile.getOriginalFilename());
            String fileName = multipartFile.getOriginalFilename();
            recognizerService.uploadImage(multipartFile, fileName);

            while(true) {
                if(results.containsKey(fileName)) {
                    result = results.get(fileName);
                    results.remove(fileName);
                    break;
                } else {
                    Message message = sqsService.consumeFromQueue(responseQueue, 20, 15);
                    if(message != null) {
                        log.info("message from response queue {}", message.getBody());
                        String messageBody = message.getBody();
                        String[] output = messageBody.split(",");
                        results.put(output[0], output[1]);
                        log.info("result: " + output[1]);
                        log.info("Deleting message from response queue");
                        sqsService.deleteMessage(message, responseQueue);
                    }
                }
            }
            responseEntity = new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Unexpected error while uploading image ", e);
            responseEntity = new ResponseEntity<String>("Unexpected Error, please try after sometime", HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return responseEntity;
    }

}
