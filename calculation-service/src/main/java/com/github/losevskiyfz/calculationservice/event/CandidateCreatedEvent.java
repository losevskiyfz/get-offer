package com.github.losevskiyfz.calculationservice.event;

import com.github.losevskiyfz.calculationservice.entity.Grade;

import java.math.BigDecimal;
import java.util.UUID;

public record CandidateCreatedEvent(
        UUID id,
        String name,
        Grade grade,
        Integer experienceYears,
        BigDecimal salary
) {
}