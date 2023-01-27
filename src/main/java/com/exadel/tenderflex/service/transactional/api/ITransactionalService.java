package com.exadel.tenderflex.service.transactional.api;

public interface ITransactionalService<TYPE> {
    TYPE saveTransactional(TYPE entity);
}
