package com.github.losevskiyfz.calculationservice.service;

import com.github.losevskiyfz.calculationservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.calculationservice.event.CandidateCreatedEvent;
import com.github.losevskiyfz.calculationservice.event.Grade;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CalculationService {

    private static final BigDecimal MARKET_COEFFICIENT = BigDecimal.ONE;

    @Cacheable(value = "calculations", key = "#event.id()")
    public CalculationCompletedEvent calculate(CandidateCreatedEvent event) {
        BigDecimal experienceCoefficient = calculateExperienceCoefficient(event.experienceYears());
        BigDecimal gradeCoefficient = calculateGradeCoefficient(event.grade());
        BigDecimal recommendedSalary = event.salary()
                .multiply(experienceCoefficient)
                .multiply(gradeCoefficient)
                .multiply(MARKET_COEFFICIENT);

        return new CalculationCompletedEvent(
                event.id(),
                event.name(),
                event.grade(),
                event.experienceYears(),
                event.salary(),
                recommendedSalary,
                experienceCoefficient,
                gradeCoefficient,
                MARKET_COEFFICIENT
        );
    }

    private BigDecimal calculateExperienceCoefficient(Integer experienceYears) {
        if (experienceYears <= 1) return new BigDecimal("0.8");
        if (experienceYears <= 4) return BigDecimal.ONE;
        return new BigDecimal("1.2");
    }

    private BigDecimal calculateGradeCoefficient(Grade grade) {
        return switch (grade) {
            case JUNIOR -> new BigDecimal("0.8");
            case MIDDLE -> BigDecimal.ONE;
            case SENIOR -> new BigDecimal("1.3");
        };
    }
}