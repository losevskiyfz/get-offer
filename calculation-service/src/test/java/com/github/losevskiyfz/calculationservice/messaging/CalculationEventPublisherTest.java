package com.github.losevskiyfz.calculationservice.messaging;

import com.github.losevskiyfz.calculationservice.base.annotation.EnableKafka;
import com.github.losevskiyfz.calculationservice.base.annotation.EnableRedis;
import com.github.losevskiyfz.calculationservice.base.initializer.KafkaContainerInitializer;
import com.github.losevskiyfz.calculationservice.config.properties.KafkaTopicsProperties;
import com.github.losevskiyfz.calculationservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.calculationservice.event.Grade;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableKafka
@EnableRedis
class CalculationEventPublisherTest {
    private static final Logger log = LoggerFactory.getLogger(CalculationEventPublisherTest.class);

    @Autowired
    private CalculationEventPublisher calculationEventPublisher;

    @Autowired
    private KafkaTopicsProperties kafkaTopicsProperties;

    private KafkaMessageListenerContainer<String, CalculationCompletedEvent> container;
    private BlockingQueue<ConsumerRecord<String, CalculationCompletedEvent>> records;
    @Value("${spring.kafka.producer.properties.spring.json.type.mapping}")
    private String typeMapping;

    @BeforeEach
    void setUp() {
        records = new LinkedBlockingQueue<>();

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                KafkaContainerInitializer.KAFKA.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        consumerProps.put(JacksonJsonDeserializer.TYPE_MAPPINGS, typeMapping);
        consumerProps.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,
                CalculationCompletedEvent.class.getName());

        KafkaTopicsProperties.TopicProperties topicProperties =
                kafkaTopicsProperties.getTopics().get("calculation-completed");

        container = new KafkaMessageListenerContainer<>(
                new DefaultKafkaConsumerFactory<>(consumerProps),
                new ContainerProperties(topicProperties.getName())
        );
        container.setupMessageListener(
                (MessageListener<String, CalculationCompletedEvent>) records::add
        );
        container.start();
        ContainerTestUtils.waitForAssignment(container, topicProperties.getPartitions());
    }

    @AfterEach
    void tearDown() {
        container.stop();
    }

    @Test
    void shouldPublishCalculationCompletedEvent() throws InterruptedException {
        UUID candidateId = UUID.randomUUID();
        CalculationCompletedEvent event = new CalculationCompletedEvent(
                candidateId, "Alice", Grade.SENIOR, 5,
                new BigDecimal("1000"), new BigDecimal("1560"),
                new BigDecimal("1.2"), new BigDecimal("1.3"), BigDecimal.ONE
        );

        calculationEventPublisher.publishCalculationCompleted(event);

        ConsumerRecord<String, CalculationCompletedEvent> record =
                records.poll(5, TimeUnit.SECONDS);

        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo(candidateId.toString());
        assertThat(record.value().candidateId()).isEqualTo(candidateId);
        assertThat(record.value().candidateName()).isEqualTo("Alice");
        assertThat(record.value().grade()).isEqualTo(Grade.SENIOR);
        assertThat(record.value().recommendedSalary()).isEqualByComparingTo("1560");
        assertThat(record.value().experienceCoefficient()).isEqualByComparingTo("1.2");
        assertThat(record.value().gradeCoefficient()).isEqualByComparingTo("1.3");
        assertThat(record.value().marketCoefficient()).isEqualByComparingTo("1.0");
    }
}