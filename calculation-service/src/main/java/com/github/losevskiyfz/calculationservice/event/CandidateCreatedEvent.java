package com.github.losevskiyfz.calculationservice.event;

import java.math.BigDecimal;
import java.util.UUID;

// TODO - decouple schema between services with Avro Registry
public record CandidateCreatedEvent(
        UUID id,
        String name,
        Grade grade,
        Integer experienceYears,
        BigDecimal salary
) {
}