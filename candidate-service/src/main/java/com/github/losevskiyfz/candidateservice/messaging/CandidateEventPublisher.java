package com.github.losevskiyfz.candidateservice.messaging;

import com.github.losevskiyfz.candidateservice.config.properties.KafkaTopicsProperties;
import com.github.losevskiyfz.candidateservice.entity.Candidate;
import com.github.losevskiyfz.candidateservice.event.CandidateCreatedEvent;
import com.github.losevskiyfz.candidateservice.mapper.CandidateMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CandidateEventPublisher {
    private final KafkaTemplate<String, CandidateCreatedEvent> kafkaTemplate;
    private final CandidateMapper candidateMapper;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public CandidateEventPublisher(
            KafkaTemplate<String, CandidateCreatedEvent> kafkaTemplate,
            CandidateMapper candidateMapper,
            KafkaTopicsProperties kafkaTopicsProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.candidateMapper = candidateMapper;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    public void publishCandidateCreated(Candidate candidate) {
        CandidateCreatedEvent event = candidateMapper.toCreatedEvent(candidate);
        kafkaTemplate.send(
                kafkaTopicsProperties.getTopics().get("candidate-created").getName(),
                candidate.getId().toString(),
                event
        );
    }
}