package com.github.losevskiyfz.calculationservice.messaging;

import com.github.losevskiyfz.calculationservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.calculationservice.event.CandidateCreatedEvent;
import com.github.losevskiyfz.calculationservice.service.CalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class CandidateEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(CandidateEventConsumer.class);

    private final CalculationService calculationService;
    private final CalculationEventPublisher calculationEventPublisher;

    public CandidateEventConsumer(
            CalculationService calculationService,
            CalculationEventPublisher calculationEventPublisher
    ) {
        this.calculationService = calculationService;
        this.calculationEventPublisher = calculationEventPublisher;
    }

    // TODO - add dead letter topic on errors
    @KafkaListener(topics = "${kafka.topics.candidate-created.name}")
    public void handle(
            @Payload CandidateCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        log.debug("Received CandidateCreatedEvent: key={}, partition={}, offset={}, id={}, name={}, grade={}",
                key, partition, offset, event.id(), event.name(), event.grade());
        CalculationCompletedEvent result = calculationService.calculate(event);
        log.debug("Calculation completed: candidateId={}, recommendedSalary={}",
                result.candidateId(), result.recommendedSalary());
        calculationEventPublisher.publishCalculationCompleted(result);
    }
}