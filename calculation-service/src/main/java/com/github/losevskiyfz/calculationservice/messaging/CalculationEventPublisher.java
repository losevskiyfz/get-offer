package com.github.losevskiyfz.calculationservice.messaging;

import com.github.losevskiyfz.calculationservice.config.properties.KafkaTopicsProperties;
import com.github.losevskiyfz.calculationservice.event.CalculationCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// TODO - implement transactional outbox
@Component
public class CalculationEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(CalculationEventPublisher.class);

    private final KafkaTemplate<String, CalculationCompletedEvent> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public CalculationEventPublisher(
            KafkaTemplate<String, CalculationCompletedEvent> kafkaTemplate,
            KafkaTopicsProperties kafkaTopicsProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    public void publishCalculationCompleted(CalculationCompletedEvent event) {
        String topic = kafkaTopicsProperties.getTopics().get("calculation-completed").getName();
        String key = event.candidateId().toString();
        log.debug("Publishing CalculationCompletedEvent: topic={}, key={}", topic, key);
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish CalculationCompletedEvent: topic={}, key={}, error={}",
                                topic, key, ex.getMessage());
                    } else {
                        log.debug("CalculationCompletedEvent published successfully: topic={}, key={}, offset={}",
                                topic, key, result.getRecordMetadata().offset());
                    }
                });
    }
}