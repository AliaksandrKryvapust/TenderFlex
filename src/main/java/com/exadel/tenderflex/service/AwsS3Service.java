package com.exadel.tenderflex.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.exadel.tenderflex.core.dto.aws.AwsS3FileDto;
import com.exadel.tenderflex.core.dto.aws.FileMetadata;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import com.exadel.tenderflex.service.api.IAwsS3Service;
import com.exadel.tenderflex.service.validator.api.IAwsS3Validator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AwsS3Service implements IAwsS3Service {
    @Value("${amazon.aws.bucket.name}")
    private String BUCKET_NAME;
    private final AmazonS3 amazonS3;
    private final IAwsS3Validator awsS3Validator;

    @Override
    public AwsS3FileDto sendFileToS3(MultipartFile file) {
        String awsFileName = UUID.randomUUID() + "." + Objects.requireNonNull(file.getContentType())
                .substring(file.getContentType().indexOf("/") + 1);
        FileMetadata metadata = new FileMetadata(Objects.requireNonNull(file.getContentType()
                .substring(file.getContentType().indexOf("/") + 1)), file.getSize());
        try {
            amazonS3.putObject(BUCKET_NAME, awsFileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to send to AWS S3 file " + file.getOriginalFilename());
        }
        String url = generateUrl(awsFileName);
        return new AwsS3FileDto(url, awsFileName);
    }

    @Override
    public String generateUrl(String awsFileName) {
        Calendar calendar = generateExpirationTime();
        awsS3Validator.validateFileKeyToStorage(BUCKET_NAME, awsFileName);
        return amazonS3.generatePresignedUrl(BUCKET_NAME, awsFileName, calendar.getTime(), HttpMethod.GET).toString();
    }

    @Override
    public Map<EFileType, AwsS3FileDto> generateUrls(Map<EFileType, MultipartFile> fileMap) {
        Map<EFileType, AwsS3FileDto> urls = new HashMap<>();
        addUrl(fileMap, EFileType.CONTRACT, urls);
        addUrl(fileMap, EFileType.AWARD_DECISION, urls);
        addUrl(fileMap, EFileType.REJECT_DECISION, urls);
        return urls;
    }

    private void addUrl(Map<EFileType, MultipartFile> fileMap, EFileType fileType, Map<EFileType, AwsS3FileDto> urls) {
        AwsS3FileDto contract = sendFileToS3(fileMap.get(fileType));
        urls.put(fileType, contract);
    }

    @NonNull
    private Calendar generateExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1); // Expiration time 1 day
        return calendar;
    }
}
