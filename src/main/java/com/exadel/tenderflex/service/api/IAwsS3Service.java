package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.core.dto.aws.AwsS3FileDto;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface IAwsS3Service {
    AwsS3FileDto sendFileToS3(MultipartFile file);
    Map<EFileType, AwsS3FileDto> generateUrls(Map<EFileType, MultipartFile> fileMap);
}
