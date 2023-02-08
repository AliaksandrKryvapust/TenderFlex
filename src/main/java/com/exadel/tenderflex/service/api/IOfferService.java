package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.repository.entity.enums.EOfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface IOfferService extends IService<Offer>, IServiceUpdate<Offer> {
    Page<Offer> getForTender(UUID id, Pageable pageable);

    Page<Offer> getForContractor(Pageable pageable);

    Set<Offer> findExpiredContractDeadline(LocalDate currentDate, EOfferStatus offerStatus);
}
