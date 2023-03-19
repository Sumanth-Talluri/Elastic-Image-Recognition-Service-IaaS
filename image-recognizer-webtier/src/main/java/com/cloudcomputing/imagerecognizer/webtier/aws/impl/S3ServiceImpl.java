package com.cloudcomputing.imagerecognizer.webtier.aws.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.cloudcomputing.imagerecognizer.webtier.aws.AwsConfig;
import com.cloudcomputing.imagerecognizer.webtier.aws.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class S3ServiceImpl implements S3Service {

    @Autowired
    AwsConfig awsConfig;

    @Value("${aws.s3.input.bucket.name}")
    private String bucketName;

    @Override
    public void uploadFile(File file, String fileName) throws Exception{
        try {
            log.info("calling s3 client to upload the file: {}", fileName);
            awsConfig.getAmazonS3Client().putObject(new PutObjectRequest(bucketName, fileName, file));
            log.info("file {} successfully uploaded to s3", fileName);
        } catch (SdkClientException e) {
            log.error("Unexpected error while uploading file: {} to S3: ", fileName, e);
            e.printStackTrace();
            throw e;
        } finally {
            file.delete();
        }
    }

    @Override
    public boolean downloadFile() {
        return false;
    }
}
