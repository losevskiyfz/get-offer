package com.github.losevskiyfz.candidateservice.messaging;

import com.github.losevskiyfz.candidateservice.config.properties.KafkaTopicsProperties;
import com.github.losevskiyfz.candidateservice.entity.Candidate;
import com.github.losevskiyfz.candidateservice.entity.Grade;
import com.github.losevskiyfz.candidateservice.event.CandidateCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(
        topics = "candidate-created",
        partitions = 1
)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest"
})
class CandidateEventPublisherTest {
    private static final Logger log = LoggerFactory.getLogger(CandidateEventPublisherTest.class);

    @Autowired
    private CandidateEventPublisher candidateEventPublisher;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaTopicsProperties kafkaTopicsProperties;

    private KafkaMessageListenerContainer<String, CandidateCreatedEvent> container;
    private BlockingQueue<ConsumerRecord<String, CandidateCreatedEvent>> records;

    @BeforeEach
    void setUp() {
        log.info("Setting up test consumer for topic: candidate-created");
        records = new LinkedBlockingQueue<>();

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                embeddedKafkaBroker, "test-group", true
        );
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        consumerProps.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, CandidateCreatedEvent.class.getName());

        DefaultKafkaConsumerFactory<String, CandidateCreatedEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps);

        KafkaTopicsProperties.TopicProperties topicProperties =
                kafkaTopicsProperties.getTopics().get("candidate-created");
        ContainerProperties containerProperties =
                new ContainerProperties(topicProperties.getName());

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setupMessageListener(
                (MessageListener<String, CandidateCreatedEvent>) records::add
        );
        container.start();

        ContainerTestUtils.waitForAssignment(container, topicProperties.getPartitions());
        log.info("Test consumer started and partition assigned");
    }

    @AfterEach
    void tearDown() {
        log.info("Stopping test consumer");
        container.stop();
        log.info("Test consumer stopped");
    }

    @Test
    void shouldPublishCandidateCreatedEvent() throws InterruptedException {
        // given
        Candidate candidate = new Candidate();
        candidate.setId(UUID.randomUUID());
        candidate.setName("Ivan");
        candidate.setGrade(Grade.JUNIOR);
        candidate.setExperienceYears(0);
        candidate.setSalary(BigDecimal.valueOf(400));
        log.info("Publishing CandidateCreatedEvent for candidate: id={}", candidate.getId());

        // when
        candidateEventPublisher.publishCandidateCreated(candidate);

        // then
        log.info("Waiting for message to arrive in topic...");
        ConsumerRecord<String, CandidateCreatedEvent> record =
                records.poll(5, TimeUnit.SECONDS);

        assertThat(record).isNotNull();
        log.info("Message received: topic={}, key={}, offset={}", record.topic(), record.key(), record.offset());
        assertThat(record.key()).isEqualTo(candidate.getId().toString());
        assertThat(record.value().id()).isEqualTo(candidate.getId());
        assertThat(record.value().name()).isEqualTo("Ivan");
        assertThat(record.value().grade()).isEqualTo(Grade.JUNIOR);
        assertThat(record.value().experienceYears()).isEqualTo(0);
        assertThat(record.value().salary()).isEqualByComparingTo(BigDecimal.valueOf(400));
    }
}