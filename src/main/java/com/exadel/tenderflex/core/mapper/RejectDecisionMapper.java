package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.input.FilesDtoInput;
import com.exadel.tenderflex.core.dto.output.RejectDecisionDtoOutput;
import com.exadel.tenderflex.repository.entity.RejectDecision;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RejectDecisionMapper {
    public RejectDecision inputMapping(FilesDtoInput filesDtoInput) {
        return RejectDecision.builder()
                .rejectDecisionFile(filesDtoInput.getRejectDecisionFile()).build(); //TODO
    }

    public RejectDecisionDtoOutput outputMapping(RejectDecision rejectDecision) {
        return RejectDecisionDtoOutput.builder()
                .id(rejectDecision.getId().toString())
                .rejectDecisionFile(rejectDecision.getRejectDecisionFile().toString())
                .dtCreate(rejectDecision.getDtCreate())
                .dtUpdate(rejectDecision.getDtUpdate()).build();
    }
}
