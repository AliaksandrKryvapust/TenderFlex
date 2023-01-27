package com.exadel.tenderflex.service;

import com.amazonaws.services.s3.AmazonS3;
import com.exadel.tenderflex.core.dto.aws.AwsS3FileDto;
import com.exadel.tenderflex.core.dto.aws.FileMetadata;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import com.exadel.tenderflex.service.api.IAwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.exadel.tenderflex.core.Constants.BUCKET_NAME;

@Service
@RequiredArgsConstructor
public class AwsS3Service implements IAwsS3Service {
    private final AmazonS3 amazonS3;

    @Override
    public AwsS3FileDto sendFileToS3(MultipartFile file) {
        String fileName = UUID.randomUUID() + file.getContentType();
        FileMetadata metadata = new FileMetadata(Objects.requireNonNull(file.getContentType()), String.valueOf(file.getSize()));
        try {
            amazonS3.putObject(BUCKET_NAME, fileName, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to send to AWS S3 file " + file.getOriginalFilename());
        }
        String url = amazonS3.getUrl(BUCKET_NAME, fileName).toString();
        return new AwsS3FileDto(url, fileName);
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

}
