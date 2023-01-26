package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.core.dto.input.TenderDtoInput;
import com.exadel.tenderflex.core.dto.output.TenderDtoOutput;
import com.exadel.tenderflex.core.dto.output.pages.TenderPageDtoOutput;

public interface ITenderManager extends IManager<TenderDtoOutput, TenderDtoInput, TenderPageDtoOutput>,
        IManagerUpdate<TenderDtoOutput, TenderDtoInput> {
}
