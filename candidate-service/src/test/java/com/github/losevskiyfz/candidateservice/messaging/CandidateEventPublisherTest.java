package com.github.losevskiyfz.candidateservice.messaging;

import com.github.losevskiyfz.candidateservice.entity.Candidate;
import com.github.losevskiyfz.candidateservice.entity.Grade;
import com.github.losevskiyfz.candidateservice.event.CandidateCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        partitions = 1,
        topics = KafkaTopics.CANDIDATE_CREATED
)
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest"
})
class CandidateEventPublisherTest {

    @Autowired
    private CandidateEventPublisher candidateEventPublisher;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaMessageListenerContainer<String, CandidateCreatedEvent> container;
    private BlockingQueue<ConsumerRecord<String, CandidateCreatedEvent>> records;

    @BeforeEach
    void setUp() {
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

        ContainerProperties containerProperties =
                new ContainerProperties(KafkaTopics.CANDIDATE_CREATED);

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setupMessageListener(
                (MessageListener<String, CandidateCreatedEvent>) records::add
        );
        container.start();

        ContainerTestUtils.waitForAssignment(container, 1);
    }

    @AfterEach
    void tearDown() {
        container.stop();
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

        // when
        candidateEventPublisher.publishCandidateCreated(candidate);

        // then
        ConsumerRecord<String, CandidateCreatedEvent> record =
                records.poll(5, TimeUnit.SECONDS);

        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo(candidate.getId().toString());
        assertThat(record.value().id()).isEqualTo(candidate.getId());
        assertThat(record.value().name()).isEqualTo("Ivan");
        assertThat(record.value().grade()).isEqualTo(Grade.JUNIOR);
        assertThat(record.value().experienceYears()).isEqualTo(0);
        assertThat(record.value().salary()).isEqualByComparingTo(BigDecimal.valueOf(400));
    }
}