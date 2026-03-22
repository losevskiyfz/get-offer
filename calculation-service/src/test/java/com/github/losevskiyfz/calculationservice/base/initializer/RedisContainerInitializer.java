package com.github.losevskiyfz.calculationservice.base.initializer;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.GenericContainer;

public class RedisContainerInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:8.6.1")
                    .withExposedPorts(6379);

    static {
        REDIS.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        TestPropertyValues.of(
                "spring.data.redis.host=" + REDIS.getHost(),
                "spring.data.redis.port=" + REDIS.getMappedPort(6379)
        ).applyTo(ctx.getEnvironment());
    }
}