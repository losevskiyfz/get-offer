package com.github.losevskiyfz.candidateservice.base.annotation;

import com.github.losevskiyfz.candidateservice.base.initializer.KafkaContainerInitializer;
import com.github.losevskiyfz.candidateservice.base.initializer.PostgresContainerInitializer;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(initializers = {
        PostgresContainerInitializer.class,
        KafkaContainerInitializer.class
})
public @interface EnablePostgresAndKafka {
}