package com.github.losevskiyfz.calculationservice.service;

import com.github.losevskiyfz.calculationservice.base.annotation.EnableRedis;
import com.github.losevskiyfz.calculationservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.calculationservice.event.CandidateCreatedEvent;
import com.github.losevskiyfz.calculationservice.event.Grade;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EnableRedis
class CalculationServiceCacheTest {

    @MockitoSpyBean
    private CalculationService calculationService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldCacheResultOnSecondCall() {
        UUID id = UUID.randomUUID();
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                id, "Alice", Grade.MIDDLE, 3, new BigDecimal("1000")
        );

        CalculationCompletedEvent first = calculationService.calculate(event);
        CalculationCompletedEvent second = calculationService.calculate(event);

        verify(calculationService, times(1)).calculate(event);
        assertThat(first).isEqualTo(second);
    }

    @Test
    void shouldReturnDifferentResultsForDifferentCandidates() {
        CandidateCreatedEvent junior = new CandidateCreatedEvent(
                UUID.randomUUID(), "Alice", Grade.JUNIOR, 1, new BigDecimal("1000")
        );
        CandidateCreatedEvent senior = new CandidateCreatedEvent(
                UUID.randomUUID(), "Bob", Grade.SENIOR, 6, new BigDecimal("1000")
        );

        CalculationCompletedEvent juniorResult = calculationService.calculate(junior);
        CalculationCompletedEvent seniorResult = calculationService.calculate(senior);

        assertThat(juniorResult.recommendedSalary())
                .isNotEqualByComparingTo(seniorResult.recommendedSalary());
    }

    @RepeatedTest(10)
    void shouldStoreCacheEntryInRedis() {
        UUID id = UUID.randomUUID();
        CandidateCreatedEvent event = new CandidateCreatedEvent(
                id, "Carol", Grade.SENIOR, 5, new BigDecimal("2000")
        );

        calculationService.calculate(event);

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() ->
                assertThat(cacheManager.getCache("calculations").get(id.toString())).isNotNull()
        );
    }
}