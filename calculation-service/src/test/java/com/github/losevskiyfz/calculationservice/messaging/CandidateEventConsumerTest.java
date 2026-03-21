package com.github.losevskiyfz.calculationservice.messaging;

import com.github.losevskiyfz.calculationservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.calculationservice.event.CandidateCreatedEvent;
import com.github.losevskiyfz.calculationservice.event.Grade;
import com.github.losevskiyfz.calculationservice.service.CalculationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateEventConsumerTest {

    @Mock
    private CalculationService calculationService;

    @Mock
    private CalculationEventPublisher calculationEventPublisher;

    @InjectMocks
    private CandidateEventConsumer candidateEventConsumer;

    @Test
    void shouldInvokeCalculationServiceOnEvent() {
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                UUID.randomUUID(), "Alice", Grade.MIDDLE, 3, new BigDecimal("1000")
        );
        CalculationCompletedEvent result = new CalculationCompletedEvent(
                event.id(), event.name(), event.grade(), event.experienceYears(),
                event.salary(), new BigDecimal("1000"),
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE
        );
        when(calculationService.calculate(event)).thenReturn(result);

        candidateEventConsumer.handle(event);

        verify(calculationService).calculate(event);
    }

    @Test
    void shouldPublishCalculationCompletedEventAfterCalculation() {
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                UUID.randomUUID(), "Bob", Grade.SENIOR, 5, new BigDecimal("2000")
        );
        CalculationCompletedEvent result = new CalculationCompletedEvent(
                event.id(), event.name(), event.grade(), event.experienceYears(),
                event.salary(), new BigDecimal("3120"),
                new BigDecimal("1.2"), new BigDecimal("1.3"), BigDecimal.ONE
        );
        when(calculationService.calculate(event)).thenReturn(result);

        candidateEventConsumer.handle(event);

        verify(calculationEventPublisher).publishCalculationCompleted(result);
    }
}