package com.github.losevskiyfz.calculationservice.messaging;

import com.github.losevskiyfz.calculationservice.event.CandidateCreatedEvent;
import com.github.losevskiyfz.calculationservice.event.Grade;
import com.github.losevskiyfz.calculationservice.service.CalculationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(
        topics = {"candidate-created", "calculation-completed"},
        partitions = 1
)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration,org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration"
})
class CandidateEventConsumerErrorHandlingTest {

    @Autowired
    private KafkaTemplate<String, CandidateCreatedEvent> kafkaTemplate;

    @MockitoBean
    private CalculationService calculationService;

    @Test
    void shouldRetryOnException() {
        doThrow(new RuntimeException("Simulated failure"))
                .when(calculationService).calculate(any());

        CandidateCreatedEvent event = new CandidateCreatedEvent(
                UUID.randomUUID(), "Alice", Grade.MIDDLE, 3, new BigDecimal("1000")
        );

        kafkaTemplate.send("candidate-created", event.id().toString(), event);

        await().atMost(30, TimeUnit.SECONDS).untilAsserted(() ->
                verify(calculationService, atLeast(2)).calculate(any())
        );
    }
}