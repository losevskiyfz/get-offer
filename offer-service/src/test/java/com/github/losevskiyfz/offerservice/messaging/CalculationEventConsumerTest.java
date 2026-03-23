package com.github.losevskiyfz.offerservice.messaging;

import com.github.losevskiyfz.offerservice.base.annotation.EnableMongoAndKafka;
import com.github.losevskiyfz.offerservice.base.initializer.KafkaContainerInitializer;
import com.github.losevskiyfz.offerservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.offerservice.event.Grade;
import com.github.losevskiyfz.offerservice.service.OfferService;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EnableMongoAndKafka
class CalculationEventConsumerTest {

    @TestConfiguration
    static class TestKafkaProducerConfig {
        @Bean
        public KafkaTemplate<String, CalculationCompletedEvent> testKafkaTemplate() {
            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                    KafkaContainerInitializer.KAFKA.getBootstrapServers());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
            return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
        }
    }

    @Autowired
    private KafkaTemplate<String, CalculationCompletedEvent> testKafkaTemplate;

    @MockitoBean
    private OfferService offerService;

    @Test
    void shouldConsumeCalculationCompletedEventAndCreateOffer() {
        UUID candidateId = UUID.randomUUID();
        CalculationCompletedEvent event = new CalculationCompletedEvent(
                candidateId, "Alice", Grade.SENIOR, 5,
                new BigDecimal("1000"), new BigDecimal("1560"),
                new BigDecimal("1.2"), new BigDecimal("1.3"), BigDecimal.ONE
        );

        testKafkaTemplate.send("calculation-completed", candidateId.toString(), event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(offerService).createOffer(any(CalculationCompletedEvent.class))
        );
    }
}