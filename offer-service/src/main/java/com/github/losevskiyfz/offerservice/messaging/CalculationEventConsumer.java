package com.github.losevskiyfz.offerservice.messaging;

import com.github.losevskiyfz.offerservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.offerservice.service.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class CalculationEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(CalculationEventConsumer.class);

    private final OfferService offerService;

    public CalculationEventConsumer(OfferService offerService) {
        this.offerService = offerService;
    }

    // TODO - add dead letter topic on errors
    @KafkaListener(topics = "${kafka.listener.topics.calculation-completed.name}")
    public void consume(
            @Payload CalculationCompletedEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.OFFSET) long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        log.debug("Received CalculationCompletedEvent: key={}, partition={}, offset={}, candidateId={}",
                key, partition, offset, event.candidateId());
        offerService.createOffer(event);
    }
}