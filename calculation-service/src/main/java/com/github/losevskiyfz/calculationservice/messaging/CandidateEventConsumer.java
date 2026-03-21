package com.github.losevskiyfz.calculationservice.messaging;

import com.github.losevskiyfz.calculationservice.event.CandidateCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CandidateEventConsumer {

    @KafkaListener(topics = "${kafka.topics.candidate-created.name}")
    public void handle(CandidateCreatedEvent event) {

    }
}