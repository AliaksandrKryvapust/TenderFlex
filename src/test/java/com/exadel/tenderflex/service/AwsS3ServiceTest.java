package com.exadel.tenderflex.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.exadel.tenderflex.core.dto.aws.AwsS3FileDto;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class AwsS3ServiceTest {
    @InjectMocks
    private AwsS3Service awsS3Service;
    @Mock
    private AmazonS3 amazonS3;
    // preconditions
    final URL awsUrl;

    {
        try {
            awsUrl = new URL("https://javatests3111222.s3.eu-central-1.amazonaws.com/CV+java+junior+Aliaksandr+Kryvapust.pdf");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void sendFileToS3() {
        // preconditions
        MockMultipartFile jsonFile = new MockMultipartFile("test.json", "", "application/json",
                "{\"key1\": \"value1\"}".getBytes());
        Mockito.when(amazonS3.getUrl(anyString(), anyString())).thenReturn(awsUrl);

        //test
        AwsS3FileDto actual = awsS3Service.sendFileToS3(jsonFile);
        Mockito.verify(amazonS3, Mockito.times(1)).putObject(anyString(), anyString(),
                any(InputStream.class), any(ObjectMetadata.class));

        // assert
        assertEquals(awsUrl.toString(), actual.getUrl());
    }

    @Test
    void generateUrls() {
        // preconditions
        MockMultipartFile jsonFile = new MockMultipartFile("test.json", "", "application/json",
                "{\"key1\": \"value1\"}".getBytes());
        Map<EFileType, MultipartFile> fileMap = new HashMap<>();
        fileMap.put(EFileType.CONTRACT, jsonFile);
        fileMap.put(EFileType.AWARD_DECISION, jsonFile);
        fileMap.put(EFileType.REJECT_DECISION, jsonFile);
        Mockito.when(amazonS3.getUrl(anyString(), anyString())).thenReturn(awsUrl);

        //test
        Map<EFileType, AwsS3FileDto> actual = awsS3Service.generateUrls(fileMap);
        Mockito.verify(amazonS3, Mockito.times(3)).putObject(anyString(), anyString(),
                any(InputStream.class), any(ObjectMetadata.class));

        // assert
        assertEquals(3, actual.size());
    }

}