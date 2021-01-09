package com.kuke.parkingticket.service.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kuke.parkingticket.advice.exception.FileConvertException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class S3FileService implements FileService {

    private final AmazonS3 amazonS3;

    private final String TEMP_FILE_PATH = "src/main/resources/";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.baseUrl}")
    private String baseUrl;


    @Override
    public String upload(MultipartFile file, String fileName) {
        File convertedFile = convert(file, fileName);
        return uploadS3(convertedFile, fileName);
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    private String uploadS3(File uploadFile, String fileName) {
        putS3(uploadFile, fileName);
        removeTempFile(uploadFile);
        return fileName;
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private File convert(MultipartFile file, String fileName) {
        File convertedFile = new File(TEMP_FILE_PATH + fileName);
        try {
            if (convertedFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
                    fos.write(file.getBytes());
                }
                return convertedFile;
            }
        } catch (IOException e) { }
        throw new FileConvertException();
    }

    private void removeTempFile(File file) {
        file.delete(); // File로 변환하며 생긴 임시파일 삭제
    }

    @Override
    public void delete(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
}
