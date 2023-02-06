package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.repository.entity.Offer;
import com.exadel.tenderflex.repository.entity.Tender;

import java.util.UUID;

public interface ITenderService extends IService<Tender>, IServiceUpdate<Tender> {
    Tender addOfferToTender(Offer offer);
}
