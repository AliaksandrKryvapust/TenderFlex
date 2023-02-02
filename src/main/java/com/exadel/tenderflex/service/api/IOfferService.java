package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.repository.entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IOfferService extends IService<Offer>, IServiceUpdate<Offer> {
    Page<Offer> getForTender(UUID id, Pageable pageable);
}
