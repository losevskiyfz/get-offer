package com.github.losevskiyfz.offerservice.event;

import java.math.BigDecimal;
import java.util.UUID;

// TODO - decouple schema between services with Avro Registry
public record CalculationCompletedEvent(
        UUID candidateId,
        String candidateName,
        Grade grade,
        Integer experienceYears,
        BigDecimal originalSalary,
        BigDecimal recommendedSalary,
        BigDecimal experienceCoefficient,
        BigDecimal gradeCoefficient,
        BigDecimal marketCoefficient
) {
}