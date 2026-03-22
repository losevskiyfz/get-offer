package com.github.losevskiyfz.offerservice.messaging;

import com.github.losevskiyfz.offerservice.base.annotation.EnableKafka;
import com.github.losevskiyfz.offerservice.base.annotation.EnableMongo;
import com.github.losevskiyfz.offerservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.offerservice.event.Grade;
import com.github.losevskiyfz.offerservice.service.OfferService;
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

@SpringBootTest
@EnableKafka
@EnableMongo
class CalculationEventConsumerTest {

    @Autowired
    private KafkaTemplate<String, CalculationCompletedEvent> kafkaTemplate;

    @MockitoBean
    private OfferService offerService;

    @Test
    void shouldConsumeCalculationCompletedEventAndCreateOffer() {
        // given
        UUID candidateId = UUID.randomUUID();
        CalculationCompletedEvent event = new CalculationCompletedEvent(
                candidateId, "Alice", Grade.SENIOR, 5,
                new BigDecimal("1000"), new BigDecimal("1560"),
                new BigDecimal("1.2"), new BigDecimal("1.3"), BigDecimal.ONE
        );

        // when
        kafkaTemplate.send("calculation-completed", candidateId.toString(), event);

        // then
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(offerService).createOffer(any(CalculationCompletedEvent.class))
        );
    }
}