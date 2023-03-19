package com.cloudcomputing.imagerecognizer.webtier.aws;

import java.io.File;

public interface S3Service {

    void uploadFile(File file, String fileName) throws Exception;

    boolean downloadFile();
}
