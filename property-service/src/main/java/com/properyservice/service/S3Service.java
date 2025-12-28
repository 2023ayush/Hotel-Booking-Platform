//package com.properyservice.service;
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//@Service
//public class S3Service {
//
//    private final AmazonS3 amazonS3;
//    private final String bucketName;
//
//    public S3Service(
//            AmazonS3 amazonS3,
//            @Value("${cloud.aws.s3.bucket-name}") String bucketName
//    ) {
//        this.amazonS3 = amazonS3;
//        this.bucketName = bucketName;
//    }
//
//    public List<String> uploadFiles(MultipartFile[] files) {
//        List<String> urls = new ArrayList<>();
//
//        for (MultipartFile file : files) {
//            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//            try {
//                ObjectMetadata metadata = new ObjectMetadata();
//                metadata.setContentLength(file.getSize());
//                metadata.setContentType(file.getContentType());
//
//                amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
//                urls.add(amazonS3.getUrl(bucketName, fileName).toString());
//
//            } catch (IOException e) {
//                throw new IllegalStateException("Failed to upload file: " + fileName, e);
//            }
//        }
//        return urls;
//    }
//}
