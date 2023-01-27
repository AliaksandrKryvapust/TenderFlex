package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.core.dto.input.TenderDtoInput;
import com.exadel.tenderflex.core.dto.output.TenderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageDtoOutput;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ITenderManager extends IManager<TenderDtoOutput, TenderDtoInput, TenderPageDtoOutput>,
        IManagerUpdate<TenderDtoOutput, TenderDtoInput> {
    TenderDtoOutput saveFormData(String tender, List<MultipartFile> files);
}
