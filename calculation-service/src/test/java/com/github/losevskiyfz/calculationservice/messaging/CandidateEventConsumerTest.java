package com.github.losevskiyfz.calculationservice.messaging;

import com.github.losevskiyfz.calculationservice.base.annotation.EnableKafka;
import com.github.losevskiyfz.calculationservice.base.annotation.EnableRedis;
import com.github.losevskiyfz.calculationservice.event.CandidateCreatedEvent;
import com.github.losevskiyfz.calculationservice.event.Grade;
import com.github.losevskiyfz.calculationservice.service.CalculationService;
import com.github.losevskiyfz.calculationservice.event.CalculationCompletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@EnableKafka
@EnableRedis
class CandidateEventConsumerTest {

    @Autowired
    private KafkaTemplate<String, CandidateCreatedEvent> kafkaTemplate;

    @MockitoBean
    private CalculationService calculationService;

    @MockitoBean
    private CalculationEventPublisher calculationEventPublisher;

    @Test
    void shouldInvokeCalculationServiceOnEvent() {
        // given
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                UUID.randomUUID(), "Alice", Grade.MIDDLE, 3, new BigDecimal("1000")
        );
        CalculationCompletedEvent result = new CalculationCompletedEvent(
                event.id(), event.name(), event.grade(), event.experienceYears(),
                event.salary(), new BigDecimal("1000"),
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE
        );
        when(calculationService.calculate(any())).thenReturn(result);

        // when
        kafkaTemplate.send("candidate-created", event.id().toString(), event);

        // then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(calculationService).calculate(any(CandidateCreatedEvent.class))
        );
    }

    @Test
    void shouldPublishCalculationCompletedEventAfterCalculation() {
        // given
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                UUID.randomUUID(), "Bob", Grade.SENIOR, 5, new BigDecimal("2000")
        );
        CalculationCompletedEvent result = new CalculationCompletedEvent(
                event.id(), event.name(), event.grade(), event.experienceYears(),
                event.salary(), new BigDecimal("3120"),
                new BigDecimal("1.2"), new BigDecimal("1.3"), BigDecimal.ONE
        );
        when(calculationService.calculate(any())).thenReturn(result);

        // when
        kafkaTemplate.send("candidate-created", event.id().toString(), event);

        // then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(calculationEventPublisher).publishCalculationCompleted(any(CalculationCompletedEvent.class))
        );
    }
}