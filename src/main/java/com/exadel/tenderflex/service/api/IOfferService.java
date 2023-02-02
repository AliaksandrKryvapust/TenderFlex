package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.repository.entity.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOfferService extends IService<Offer>, IServiceUpdate<Offer> {
    Page<Offer> getForTender(String id, Pageable pageable);
}
