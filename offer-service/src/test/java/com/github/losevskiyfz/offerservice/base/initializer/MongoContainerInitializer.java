package com.github.losevskiyfz.offerservice.base.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.test.util.TestPropertyValues;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class MongoContainerInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final MongoDBContainer MONGO =
            new MongoDBContainer(DockerImageName.parse("mongo:8.2.6"));

    static {
        MONGO.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        TestPropertyValues.of(
                "spring.mongodb.uri=" + MONGO.getReplicaSetUrl("offer_service")
        ).applyTo(ctx.getEnvironment());
    }
}