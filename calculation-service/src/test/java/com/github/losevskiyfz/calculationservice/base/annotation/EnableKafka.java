package com.github.losevskiyfz.calculationservice.base.annotation;

import com.github.losevskiyfz.calculationservice.base.initializer.KafkaContainerInitializer;
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
        "kafka.topics.candidate-created.partitions=1",
        "kafka.topics.candidate-created.replicas=1",
        "kafka.topics.calculation-completed.replicas=1"
})
public @interface EnableKafka {
}