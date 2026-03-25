package com.github.losevskiyfz.offerservice.service;

import com.github.losevskiyfz.offerservice.entity.Offer;
import com.github.losevskiyfz.offerservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.offerservice.event.Grade;
import com.github.losevskiyfz.offerservice.exception.OfferNotFoundException;
import com.github.losevskiyfz.offerservice.mapper.OfferMapper;
import com.github.losevskiyfz.offerservice.repository.OfferRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OfferMapper offerMapper;

    @InjectMocks
    private OfferService offerService;

    private CalculationCompletedEvent buildEvent(UUID candidateId) {
        return new CalculationCompletedEvent(
                candidateId, "Alice", Grade.SENIOR, 5,
                new BigDecimal("1000"), new BigDecimal("1560"),
                new BigDecimal("1.2"), new BigDecimal("1.3"), BigDecimal.ONE
        );
    }

    private Offer buildOffer(UUID candidateId) {
        Offer offer = new Offer();
        offer.setId(UUID.randomUUID().toString());
        offer.setCandidateId(candidateId);
        offer.setCandidateName("Alice");
        offer.setGrade(Grade.SENIOR);
        offer.setExperienceYears(5);
        offer.setOriginalSalary(new BigDecimal("1000"));
        offer.setRecommendedSalary(new BigDecimal("1560"));
        offer.setExperienceCoefficient(new BigDecimal("1.2"));
        offer.setGradeCoefficient(new BigDecimal("1.3"));
        offer.setMarketCoefficient(BigDecimal.ONE);
        offer.setCreatedAt(Instant.now());
        return offer;
    }

    @Test
    void shouldCreateOfferFromEvent() {
        // given
        UUID candidateId = UUID.randomUUID();
        CalculationCompletedEvent event = buildEvent(candidateId);
        Offer mapped = buildOffer(candidateId);
        Offer saved = buildOffer(candidateId);

        when(offerMapper.toOffer(event)).thenReturn(mapped);
        when(offerRepository.save(mapped)).thenReturn(saved);

        // when
        Offer result = offerService.createOffer(event);

        // then
        assertThat(result).isEqualTo(saved);
        verify(offerMapper).toOffer(event);
        verify(offerRepository).save(mapped);
    }

    @Test
    void shouldSetCreatedAtWhenCreatingOffer() {
        // given
        UUID candidateId = UUID.randomUUID();
        CalculationCompletedEvent event = buildEvent(candidateId);
        Offer mapped = buildOffer(candidateId);
        mapped.setCreatedAt(null);

        when(offerMapper.toOffer(event)).thenReturn(mapped);
        when(offerRepository.save(any())).thenReturn(mapped);

        // when
        offerService.createOffer(event);

        // then
        ArgumentCaptor<Offer> captor = ArgumentCaptor.forClass(Offer.class);
        verify(offerRepository).save(captor.capture());
        assertThat(captor.getValue().getCreatedAt()).isNotNull();
    }

    @Test
    void shouldReturnPageOfOffers() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Offer offer = buildOffer(UUID.randomUUID());
        Page<Offer> page = new PageImpl<>(List.of(offer), pageable, 1);

        when(offerRepository.findAll(pageable)).thenReturn(page);

        // when
        Page<Offer> result = offerService.getOffers(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).containsExactly(offer);
        verify(offerRepository).findAll(pageable);
    }

    @Test
    void shouldReturnLatestOffer() {
        // given
        UUID candidateId = UUID.randomUUID();
        Offer offer = buildOffer(candidateId);

        when(offerRepository.findTopByCandidateIdOrderByCreatedAtDesc(candidateId))
                .thenReturn(Optional.of(offer));

        // when
        Offer result = offerService.getLatestOffer(candidateId);

        // then
        assertThat(result).isEqualTo(offer);
        verify(offerRepository).findTopByCandidateIdOrderByCreatedAtDesc(candidateId);
    }

    @Test
    void shouldThrowOfferNotFoundExceptionWhenNoOfferForCandidate() {
        // given
        UUID candidateId = UUID.randomUUID();

        when(offerRepository.findTopByCandidateIdOrderByCreatedAtDesc(candidateId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> offerService.getLatestOffer(candidateId))
                .isInstanceOf(OfferNotFoundException.class);
        verify(offerRepository).findTopByCandidateIdOrderByCreatedAtDesc(candidateId);
        verify(offerMapper, never()).toOffer(any());
    }
}