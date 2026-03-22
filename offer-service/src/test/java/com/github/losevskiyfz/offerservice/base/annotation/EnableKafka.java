package com.github.losevskiyfz.offerservice.base.annotation;

import com.github.losevskiyfz.offerservice.base.initializer.KafkaContainerInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(initializers = KafkaContainerInitializer.class)
@TestPropertySource(properties = {
        "kafka.topics.calculation-completed.partitions=1",
        "kafka.topics.calculation-completed.replicas=1",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JacksonJsonSerializer",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer"
})
public @interface EnableKafka {
}