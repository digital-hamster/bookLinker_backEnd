package com.books_recommend.book_recommend.common.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@Slf4j
@RequiredArgsConstructor    // final 멤버변수가 있으면 생성자 항목에 포함시킴
@Component
@Service
public class S3Uploader {
    private final AmazonS3 amazonS3Client;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) {
        try {
            var inputStream = multipartFile.getInputStream();
            var fileName = dirName + "/" + multipartFile.getOriginalFilename();
            var contentType = multipartFile.getContentType();

            return putS3(inputStream, fileName, contentType);
        } catch (IOException e) {
            log.error("S3 업로드 중 오류 발생 dirName: {}, fileName: {}", dirName, multipartFile.getOriginalFilename());
            throw new RuntimeException("S3 업로드 중 오류 발생", e);
        }
    }

    private String putS3(InputStream inputStream, String dirName, String contentType) {
        var objMeta = new ObjectMetadata();
        objMeta.setContentType(contentType);

        amazonS3Client.putObject(
            new PutObjectRequest(bucket, dirName, inputStream, objMeta)
                .withCannedAcl(CannedAccessControlList.PublicRead)
        );
        return amazonS3Client.getUrl(bucket, dirName).toString();
    }

//    public String getS3(String bucket, String fileName) {
//        return amazonS3Client.getUrl(bucket, fileName).toString();
//    }
}


