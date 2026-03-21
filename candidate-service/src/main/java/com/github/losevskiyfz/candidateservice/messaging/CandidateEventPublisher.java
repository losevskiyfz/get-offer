package com.github.losevskiyfz.candidateservice.messaging;

import com.github.losevskiyfz.candidateservice.entity.Candidate;
import com.github.losevskiyfz.candidateservice.event.CandidateCreatedEvent;
import com.github.losevskiyfz.candidateservice.mapper.CandidateMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CandidateEventPublisher {
    private final KafkaTemplate<String, CandidateCreatedEvent> kafkaTemplate;
    private final CandidateMapper candidateMapper;

    public CandidateEventPublisher(
            KafkaTemplate<String, CandidateCreatedEvent> kafkaTemplate,
            CandidateMapper candidateMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.candidateMapper = candidateMapper;
    }

    public void publishCandidateCreated(Candidate candidate) {
        CandidateCreatedEvent event = candidateMapper.toCreatedEvent(candidate);
        kafkaTemplate.send(KafkaTopics.CANDIDATE_CREATED, candidate.getId().toString(), event);
    }
}