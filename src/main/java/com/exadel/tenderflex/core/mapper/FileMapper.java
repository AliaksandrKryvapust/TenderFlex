package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.output.FileDtoOutput;
import com.exadel.tenderflex.repository.entity.File;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FileMapper {
    Set<File> inputContractMapping(Map<EFileType, MultipartFile> dtoInput, Map<EFileType, String> urls) {
        Set<File> files = new HashSet<>();
        MultipartFile fileContract = dtoInput.get(EFileType.CONTRACT);
        File contract = File.builder()
                .contentType(fileContract.getContentType())
                .fileType(EFileType.CONTRACT)
                .fileName(fileContract.getOriginalFilename())
                .url(urls.get(EFileType.CONTRACT)).build();
        files.add(contract);
        MultipartFile fileAward = dtoInput.get(EFileType.AWARD_DECISION);
        File award = File.builder()
                .contentType(fileAward.getContentType())
                .fileType(EFileType.AWARD_DECISION)
                .fileName(fileAward.getOriginalFilename())
                .url(urls.get(EFileType.AWARD_DECISION)).build();
        files.add(award);
        return files;
    }

    File inputRejectMapping(Map<EFileType, MultipartFile> dtoInput, Map<EFileType, String> urls) {
        MultipartFile fileReject = dtoInput.get(EFileType.REJECT_DECISION);
        return File.builder()
                .contentType(fileReject.getContentType())
                .fileType(EFileType.REJECT_DECISION)
                .fileName(fileReject.getOriginalFilename())
                .url(urls.get(EFileType.REJECT_DECISION)).build();
    }

    FileDtoOutput outputMapping(File file) {
        return FileDtoOutput.builder()
                .id(file.getId())
                .contentType(file.getContentType())
                .fileType(file.getFileType().name())
                .fileName(file.getFileName())
                .url(file.getUrl())
                .dtCreate(file.getDtCreate())
                .dtUpdate(file.getDtUpdate()).build();
    }

    Set<FileDtoOutput> outputSetMapping(Set<File> files) {
        return files.stream().map(this::outputMapping).collect(Collectors.toSet());
    }
}
