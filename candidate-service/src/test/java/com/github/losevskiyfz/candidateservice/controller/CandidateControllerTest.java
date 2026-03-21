package com.github.losevskiyfz.candidateservice.controller;

import com.github.losevskiyfz.candidateservice.dto.CandidateRequest;
import com.github.losevskiyfz.candidateservice.dto.CandidateResponse;
import com.github.losevskiyfz.candidateservice.entity.Grade;
import com.github.losevskiyfz.candidateservice.exception.CandidateNotFoundException;
import com.github.losevskiyfz.candidateservice.service.CandidateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(CandidateController.class)
@AutoConfigureRestTestClient
public class CandidateControllerTest {
    @Autowired
    private RestTestClient restTestClient;
    @MockitoBean
    private CandidateService candidateService;
    private final String BASE_URI = "/api/v1/candidates";

    @Nested
    @DisplayName("POST " + BASE_URI)
    class CreateCandidate {
        @Test
        void shouldReturn201OnSaveCandidate() {
            // given
            String name = "Ivan";
            Grade grade = Grade.JUNIOR;
            Integer experienceYears = 0;
            BigDecimal salary = BigDecimal.valueOf(400);
            UUID id = UUID.randomUUID();
            CandidateRequest candidateRequest = new CandidateRequest(
                    name,
                    grade,
                    experienceYears,
                    salary
            );
            when(candidateService.create(any(CandidateRequest.class)))
                    .thenReturn(new CandidateResponse(id, name, grade, experienceYears, salary));

            // when & then
            restTestClient.post()
                    .uri("/api/v1/candidates")
                    .body(candidateRequest)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectHeader().location("http://localhost/api/v1/candidates/" + id)
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.name").isEqualTo("Ivan")
                    .jsonPath("$.grade").isEqualTo(Grade.JUNIOR.name())
                    .jsonPath("$.salary").isEqualTo(BigDecimal.valueOf(400));
        }

        @Test
        void shouldReturn400onGradeValidationError() {

        }

        @Test
        void shouldReturn400onNameValidationError() {

        }

        @Test
        void shouldReturn400onSalaryValidationError() {

        }

        @Test
        void shouldReturn400onExperienceYearsValidationError() {

        }
    }

    @Nested
    @DisplayName("GET " + BASE_URI + "/{id}")
    class GetCandidate {
        private final String BASE_GET_URI = BASE_URI + "/{id}";
        @Test
        void shouldReturn200WithCandidate() {
            // given
            UUID id = UUID.randomUUID();
            String name = "Ivan";
            Grade grade = Grade.JUNIOR;
            Integer experienceYears = 0;
            BigDecimal salary = BigDecimal.valueOf(400);

            when(candidateService.getById(id))
                    .thenReturn(new CandidateResponse(id, name, grade, experienceYears, salary));

            // when & then
            restTestClient.get()
                    .uri(BASE_GET_URI.replace("{id}", id.toString()))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(id.toString())
                    .jsonPath("$.name").isEqualTo(name)
                    .jsonPath("$.grade").isEqualTo(grade.name())
                    .jsonPath("$.experienceYears").isEqualTo(experienceYears)
                    .jsonPath("$.salary").isEqualTo(salary);
        }

        @Test
        void shouldReturn404WhenCandidateNotFound() {
            // given
            UUID id = UUID.randomUUID();

            when(candidateService.getById(id))
                    .thenThrow(new CandidateNotFoundException(id));

            // when & then
            restTestClient.get()
                    .uri(BASE_GET_URI.replace("{id}", id.toString()))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        void shouldReturn400WhenIdIsNotValidUUID() {
            // when & then
            restTestClient.get()
                    .uri(BASE_GET_URI.replace("{id}", "not-a-valid-uuid"))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }
}
