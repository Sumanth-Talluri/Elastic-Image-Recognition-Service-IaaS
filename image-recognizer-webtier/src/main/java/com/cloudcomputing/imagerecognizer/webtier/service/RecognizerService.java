package com.cloudcomputing.imagerecognizer.webtier.service;

import org.springframework.web.multipart.MultipartFile;

public interface RecognizerService {

    void uploadImage(MultipartFile multipartFile, String fileName) throws Exception;
}
