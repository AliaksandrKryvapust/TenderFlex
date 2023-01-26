package com.exadel.tenderflex.core.mapper;

import com.exadel.tenderflex.core.dto.output.OfferDtoOutput;
import com.exadel.tenderflex.repository.entity.Offer;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OfferMapper {

    public OfferDtoOutput outputMapping(Offer offer){
        return OfferDtoOutput.builder().build(); //TODO
    }

    public Set<OfferDtoOutput> listOutputMapping(Set<Offer> offers){
        return offers.stream().map(this::outputMapping).collect(Collectors.toSet());
    }
}
