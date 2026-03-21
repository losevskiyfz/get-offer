package com.github.losevskiyfz.calculationservice.base;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public abstract class RedisIntegrationTest {

    static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:8.6.1")
                    .withExposedPorts(6379)
                    .withReuse(true);

    static {
        REDIS.start();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }
}