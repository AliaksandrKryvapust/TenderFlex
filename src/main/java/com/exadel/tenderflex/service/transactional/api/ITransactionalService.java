package com.exadel.tenderflex.service.transactional.api;

import com.exadel.tenderflex.core.dto.input.ActionDto;

public interface ITransactionalService<TYPE> {
    TYPE saveTransactional(TYPE entity);
    TYPE awardTransactionalAction(ActionDto actionDto);
}
