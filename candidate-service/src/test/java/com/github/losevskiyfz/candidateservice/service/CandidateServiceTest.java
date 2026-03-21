package com.github.losevskiyfz.candidateservice.service;

import com.github.losevskiyfz.candidateservice.dto.CandidateRequest;
import com.github.losevskiyfz.candidateservice.dto.CandidateResponse;
import com.github.losevskiyfz.candidateservice.entity.Candidate;
import com.github.losevskiyfz.candidateservice.entity.Grade;
import com.github.losevskiyfz.candidateservice.exception.CandidateNotFoundException;
import com.github.losevskiyfz.candidateservice.mapper.CandidateMapper;
import com.github.losevskiyfz.candidateservice.repository.CandidateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    CandidateRepository candidateRepository;

    @Mock
    CandidateMapper candidateMapper;

    @InjectMocks
    CandidateService candidateService;

    @Test
    void shouldSaveCandidateAndReturnResponse() {
        // given
        String name = "Ivan";
        Grade grade = Grade.JUNIOR;
        Integer experienceYears = 0;
        BigDecimal salary = BigDecimal.valueOf(400);
        UUID id = UUID.randomUUID();
        CandidateRequest request = new CandidateRequest(name, grade, experienceYears, salary);
        Candidate entity = new Candidate();
        Candidate savedEntity = new Candidate();
        CandidateResponse expectedResponse = new CandidateResponse(id, name, grade, experienceYears, salary);

        when(candidateMapper.toEntity(request)).thenReturn(entity);
        when(candidateRepository.save(entity)).thenReturn(savedEntity);
        when(candidateMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // when
        CandidateResponse actual = candidateService.create(request);

        // then
        assertThat(actual).isEqualTo(expectedResponse);
        verify(candidateMapper).toEntity(request);
        verify(candidateRepository).save(entity);
        verify(candidateMapper).toResponse(savedEntity);
    }

    @Test
    void shouldReturnCandidateResponse() {
        // given
        UUID id = UUID.randomUUID();
        Candidate entity = new Candidate();
        CandidateResponse expectedResponse = new CandidateResponse(id, "Ivan", Grade.JUNIOR, 0, BigDecimal.valueOf(400));

        when(candidateRepository.findById(id)).thenReturn(Optional.of(entity));
        when(candidateMapper.toResponse(entity)).thenReturn(expectedResponse);

        // when
        CandidateResponse actual = candidateService.getById(id);

        // then
        assertThat(actual).isEqualTo(expectedResponse);
        verify(candidateRepository).findById(id);
        verify(candidateMapper).toResponse(entity);
    }

    @Test
    void shouldThrowCandidateNotFoundExceptionWhenCandidateDoesNotExist() {
        // given
        UUID id = UUID.randomUUID();

        when(candidateRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> candidateService.getById(id))
                .isInstanceOf(CandidateNotFoundException.class);
        verify(candidateRepository).findById(id);
        verify(candidateMapper, never()).toResponse(any());
    }
}