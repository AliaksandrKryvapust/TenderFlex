package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.repository.entity.Tender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITenderService extends IService<Tender>, IServiceUpdate<Tender> {
    Page<Tender> getAll(Pageable pageable);
}
