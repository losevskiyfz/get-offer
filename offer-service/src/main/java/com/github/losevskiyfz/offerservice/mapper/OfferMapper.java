package com.github.losevskiyfz.offerservice.mapper;

import com.github.losevskiyfz.offerservice.entity.Offer;
import com.github.losevskiyfz.offerservice.event.CalculationCompletedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OfferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Offer toOffer(CalculationCompletedEvent event);
}