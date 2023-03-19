package com.cloudcomputing.imagerecognizer.webtier.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class FileUtils {

    public static File convertToFile(MultipartFile multipartFile) {

        String fileName = multipartFile.getOriginalFilename() == null ? multipartFile.getName() : multipartFile.getOriginalFilename();
        File file = new File(fileName);
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(multipartFile.getBytes());

        } catch (FileNotFoundException e) {
            log.error("Multipart file not found: ", e);
            e.printStackTrace();

        } catch (IOException e) {
            log.error("Unexpected error while converting MultiPartFile to File: ", e);
            e.printStackTrace();
        }

        return file;
    }

}
