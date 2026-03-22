package com.github.losevskiyfz.offerservice.base.initializer;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaContainerInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final KafkaContainer KAFKA =
            new KafkaContainer(DockerImageName.parse("apache/kafka:4.2.0"));

    static {
        KAFKA.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        TestPropertyValues.of(
                "spring.kafka.producer.bootstrap-servers=" + KAFKA.getBootstrapServers(),
                "spring.kafka.consumer.bootstrap-servers=" + KAFKA.getBootstrapServers(),
                "spring.kafka.consumer.auto-offset-reset=earliest"
        ).applyTo(ctx.getEnvironment());
    }
}