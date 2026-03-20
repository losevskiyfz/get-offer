package com.github.losevskiyfz.candidateservice.controller;

import com.github.losevskiyfz.candidateservice.dto.CandidateRequest;
import com.github.losevskiyfz.candidateservice.dto.CandidateResponse;
import com.github.losevskiyfz.candidateservice.entity.Grade;
import com.github.losevskiyfz.candidateservice.service.CandidateService;
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

        // when
        // then
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
    void shouldReturn400onGradeValidationError(){

    }

    @Test
    void shouldReturn400onNameValidationError(){

    }

    @Test
    void shouldReturn400onSalaryValidationError(){

    }

    @Test
    void shouldReturn400onExperienceYearsValidationError(){

    }
}
