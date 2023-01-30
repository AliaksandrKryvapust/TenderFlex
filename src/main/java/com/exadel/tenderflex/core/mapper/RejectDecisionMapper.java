package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.aws.AwsS3FileDto;
import com.exadel.tenderflex.core.dto.output.FileDtoOutput;
import com.exadel.tenderflex.core.dto.output.RejectDecisionDtoOutput;
import com.exadel.tenderflex.repository.entity.File;
import com.exadel.tenderflex.repository.entity.RejectDecision;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class RejectDecisionMapper {
    private final FileMapper fileMapper;

    public RejectDecision inputMapping(Map<EFileType, MultipartFile> dtoInput, Map<EFileType, AwsS3FileDto> urls) {
        File file = fileMapper.inputRejectMapping(dtoInput, urls);
        return RejectDecision.builder()
                .file(file)
                .build();
    }

    public RejectDecisionDtoOutput outputMapping(RejectDecision rejectDecision) {
        if (rejectDecision.getFile() != null) {
            FileDtoOutput dtoOutput = fileMapper.outputMapping(rejectDecision.getFile());
            return RejectDecisionDtoOutput.builder()
                    .id(rejectDecision.getId().toString())
                    .rejectDecision(dtoOutput)
                    .dtCreate(rejectDecision.getDtCreate())
                    .dtUpdate(rejectDecision.getDtUpdate())
                    .build();
        } else {
            return RejectDecisionDtoOutput.builder()
                    .id(rejectDecision.getId().toString())
                    .dtCreate(rejectDecision.getDtCreate())
                    .dtUpdate(rejectDecision.getDtUpdate())
                    .build();
        }
    }
}
