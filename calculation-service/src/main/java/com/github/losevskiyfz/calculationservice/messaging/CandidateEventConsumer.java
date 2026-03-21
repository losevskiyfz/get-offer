package com.github.losevskiyfz.calculationservice.messaging;

import com.github.losevskiyfz.calculationservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.calculationservice.event.CandidateCreatedEvent;
import com.github.losevskiyfz.calculationservice.service.CalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
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
    public void handle(CandidateCreatedEvent event) {
        log.debug("Received CandidateCreatedEvent: id={}, name={}, grade={}",
                event.id(), event.name(), event.grade());
        CalculationCompletedEvent result = calculationService.calculate(event);
        log.debug("Calculation completed: candidateId={}, recommendedSalary={}",
                result.candidateId(), result.recommendedSalary());
        calculationEventPublisher.publishCalculationCompleted(result);
    }
}