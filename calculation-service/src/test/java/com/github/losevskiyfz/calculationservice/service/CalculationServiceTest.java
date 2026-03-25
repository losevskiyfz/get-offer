package com.github.losevskiyfz.calculationservice.service;

import com.github.losevskiyfz.calculationservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.calculationservice.event.CandidateCreatedEvent;
import com.github.losevskiyfz.calculationservice.event.Grade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CalculationServiceTest {

    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new CalculationService();
    }

    @Test
    void shouldApplyJuniorGradeCoefficientAndLowExperience() {
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                UUID.randomUUID(), "Alice", Grade.JUNIOR, 1, new BigDecimal("1000")
        );

        CalculationCompletedEvent result = calculationService.calculate(event);

        assertThat(result.experienceCoefficient()).isEqualByComparingTo("0.8");
        assertThat(result.gradeCoefficient()).isEqualByComparingTo("0.8");
        assertThat(result.recommendedSalary()).isEqualByComparingTo("640.00");
    }

    @Test
    void shouldApplyMiddleGradeCoefficientAndMidExperience() {
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                UUID.randomUUID(), "Bob", Grade.MIDDLE, 3, new BigDecimal("1000")
        );

        CalculationCompletedEvent result = calculationService.calculate(event);

        assertThat(result.experienceCoefficient()).isEqualByComparingTo("1.0");
        assertThat(result.gradeCoefficient()).isEqualByComparingTo("1.0");
        assertThat(result.recommendedSalary()).isEqualByComparingTo("1000.00");
    }

    @Test
    void shouldApplySeniorGradeCoefficientAndHighExperience() {
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                UUID.randomUUID(), "Carol", Grade.SENIOR, 5, new BigDecimal("1000")
        );

        CalculationCompletedEvent result = calculationService.calculate(event);

        assertThat(result.experienceCoefficient()).isEqualByComparingTo("1.2");
        assertThat(result.gradeCoefficient()).isEqualByComparingTo("1.3");
        assertThat(result.recommendedSalary()).isEqualByComparingTo("1560.00");
    }

    @Test
    void shouldPreserveOriginalSalary() {
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                UUID.randomUUID(), "Dave", Grade.MIDDLE, 3, new BigDecimal("2500")
        );

        CalculationCompletedEvent result = calculationService.calculate(event);

        assertThat(result.originalSalary()).isEqualByComparingTo("2500");
    }

    @Test
    void shouldPreserveCandidateMetadata() {
        UUID id = UUID.randomUUID();
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                id, "Eve", Grade.SENIOR, 6, new BigDecimal("3000")
        );

        CalculationCompletedEvent result = calculationService.calculate(event);

        assertThat(result.candidateId()).isEqualTo(id);
        assertThat(result.candidateName()).isEqualTo("Eve");
        assertThat(result.grade()).isEqualTo(Grade.SENIOR);
        assertThat(result.experienceYears()).isEqualTo(6);
    }

    @Test
    void shouldApplyMarketCoefficientOfOne() {
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                UUID.randomUUID(), "Frank", Grade.MIDDLE, 3, new BigDecimal("1000")
        );

        CalculationCompletedEvent result = calculationService.calculate(event);

        assertThat(result.marketCoefficient()).isEqualByComparingTo("1.0");
    }
}