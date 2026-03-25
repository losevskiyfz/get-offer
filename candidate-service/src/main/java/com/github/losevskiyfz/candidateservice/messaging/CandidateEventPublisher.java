package com.github.losevskiyfz.candidateservice.messaging;

import com.github.losevskiyfz.candidateservice.config.properties.KafkaTopicsProperties;
import com.github.losevskiyfz.candidateservice.entity.Candidate;
import com.github.losevskiyfz.candidateservice.event.CandidateCreatedEvent;
import com.github.losevskiyfz.candidateservice.mapper.CandidateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// TODO - implement transactional outbox
@Component
public class CandidateEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(CandidateEventPublisher.class);

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
        String topic = kafkaTopicsProperties.getTopics().get("candidate-created").getName();
        String key = candidate.getId().toString();
        log.debug("Publishing CandidateCreatedEvent: topic={}, key={}", topic, key);
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish CandidateCreatedEvent: topic={}, key={}, error={}",
                                topic, key, ex.getMessage());
                    } else {
                        log.debug("CandidateCreatedEvent published successfully: topic={}, key={}, offset={}",
                                topic, key, result.getRecordMetadata().offset());
                    }
                });
    }
}