package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.input.FilesDtoInput;
import com.exadel.tenderflex.core.dto.output.ContractDtoOutput;
import com.exadel.tenderflex.repository.entity.Contract;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ContractMapper {
    public Contract inputMapping(FilesDtoInput dtoInput) {
        return Contract.builder()
                .contractFile(dtoInput.getContractFile()) //TODO
                .awardDecisionFile(dtoInput.getAwardDecisionFile()) //TODO
                .contractDeadline(dtoInput.getContractDeadline()).build();
    }

    public ContractDtoOutput outputMapping(Contract contract) {
        if (contract.getContractFile() != null && contract.getAwardDecisionFile() != null) {
            return ContractDtoOutput.builder()
                    .id(contract.getId().toString())
                    .contractFile(contract.getContractFile().toString())
                    .awardDecisionFile(contract.getAwardDecisionFile().toString())
                    .contractDeadline(contract.getContractDeadline())
                    .dtCreate(contract.getDtCreate())
                    .dtUpdate(contract.getDtUpdate()).build();
        } else if (contract.getContractFile() != null) {
            return ContractDtoOutput.builder()
                    .id(contract.getId().toString())
                    .contractFile(contract.getContractFile().toString())
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
