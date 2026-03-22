package com.github.losevskiyfz.offerservice.controller;

import com.github.losevskiyfz.offerservice.entity.Offer;
import com.github.losevskiyfz.offerservice.event.Grade;
import com.github.losevskiyfz.offerservice.exception.OfferNotFoundException;
import com.github.losevskiyfz.offerservice.service.OfferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(OfferController.class)
@AutoConfigureRestTestClient
class OfferControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private OfferService offerService;

    private final String BASE_URI = OfferController.BASE_URL;

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

    @Nested
    @DisplayName("GET " + "/api/v1/offers")
    class GetOffers {

        @Test
        void shouldReturn200WithPageOfOffers() {
            // given
            UUID candidateId = UUID.randomUUID();
            Offer offer = buildOffer(candidateId);
            PageImpl<Offer> page = new PageImpl<>(List.of(offer), PageRequest.of(0, 20), 1);

            when(offerService.getOffers(any())).thenReturn(page);

            // when & then
            restTestClient.get()
                    .uri(BASE_URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.content").isArray()
                    .jsonPath("$.content[0].candidateId").isEqualTo(candidateId.toString())
                    .jsonPath("$.content[0].candidateName").isEqualTo("Alice")
                    .jsonPath("$.content[0].grade").isEqualTo(Grade.SENIOR.name())
                    .jsonPath("$.page.totalElements").isEqualTo(1);
        }

        @Test
        void shouldReturn200WithEmptyPage() {
            // given
            when(offerService.getOffers(any()))
                    .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

            // when & then
            restTestClient.get()
                    .uri(BASE_URI)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.content").isArray()
                    .jsonPath("$.page.totalElements").isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("GET " + "/api/v1/offers" + "/candidates/{candidateId}")
    class GetLatestOffer {
        private final String CANDIDATE_URI = BASE_URI + "/candidates/{candidateId}";

        @Test
        void shouldReturn200WithLatestOffer() {
            // given
            UUID candidateId = UUID.randomUUID();
            Offer offer = buildOffer(candidateId);

            when(offerService.getLatestOffer(candidateId)).thenReturn(offer);

            // when & then
            restTestClient.get()
                    .uri(CANDIDATE_URI.replace("{candidateId}", candidateId.toString()))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.candidateId").isEqualTo(candidateId.toString())
                    .jsonPath("$.candidateName").isEqualTo("Alice")
                    .jsonPath("$.grade").isEqualTo(Grade.SENIOR.name())
                    .jsonPath("$.recommendedSalary").isEqualTo(1560);
        }

        @Test
        void shouldReturn404WhenOfferNotFound() {
            // given
            UUID candidateId = UUID.randomUUID();

            when(offerService.getLatestOffer(candidateId))
                    .thenThrow(new OfferNotFoundException(candidateId));

            // when & then
            restTestClient.get()
                    .uri(CANDIDATE_URI.replace("{candidateId}", candidateId.toString()))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void shouldReturn400WhenCandidateIdIsNotValidUUID() {
            // when & then
            restTestClient.get()
                    .uri(CANDIDATE_URI.replace("{candidateId}", "not-a-valid-uuid"))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }
}