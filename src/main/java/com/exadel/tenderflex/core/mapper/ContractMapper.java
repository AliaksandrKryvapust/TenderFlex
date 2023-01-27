package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.aws.AwsS3FileDto;
import com.exadel.tenderflex.core.dto.output.ContractDtoOutput;
import com.exadel.tenderflex.core.dto.output.FileDtoOutput;
import com.exadel.tenderflex.repository.entity.Contract;
import com.exadel.tenderflex.repository.entity.File;
import com.exadel.tenderflex.repository.entity.enums.EFileType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
public class ContractMapper {
    private final FileMapper fileMapper;

    public Contract inputMapping(LocalDate contractDeadline, Map<EFileType, MultipartFile> dtoInput,
                                 Map<EFileType, AwsS3FileDto> urls) {
        Set<File> files = fileMapper.inputContractMapping(dtoInput, urls);
        return Contract.builder()
                .files(files)
                .contractDeadline(contractDeadline).build();
    }

    public ContractDtoOutput outputMapping(Contract contract) {
        if (contract.getFiles() != null) {
            Set<FileDtoOutput> outputs = fileMapper.outputSetMapping(contract.getFiles());
            return ContractDtoOutput.builder()
                    .id(contract.getId().toString())
                    .files(outputs)
                    .contractDeadline(contract.getContractDeadline())
                    .dtCreate(contract.getDtCreate())
                    .dtUpdate(contract.getDtUpdate()).build();
        } else {
            return ContractDtoOutput.builder()
                    .id(contract.getId().toString())
                    .contractDeadline(contract.getContractDeadline())
                    .dtCreate(contract.getDtCreate())
                    .dtUpdate(contract.getDtUpdate()).build();
        }
    }
}
