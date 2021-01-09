package com.kuke.parkingticket.service.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String upload(MultipartFile file, String fileName); // return filename
    String getBaseUrl();
    void delete(String fileName);
}
