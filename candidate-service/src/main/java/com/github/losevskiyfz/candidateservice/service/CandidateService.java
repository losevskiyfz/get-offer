package com.github.losevskiyfz.candidateservice.service;

import com.github.losevskiyfz.candidateservice.dto.CandidateRequest;
import com.github.losevskiyfz.candidateservice.dto.CandidateResponse;
import com.github.losevskiyfz.candidateservice.entity.Candidate;
import com.github.losevskiyfz.candidateservice.exception.CandidateNotFoundException;
import com.github.losevskiyfz.candidateservice.mapper.CandidateMapper;
import com.github.losevskiyfz.candidateservice.messaging.CandidateEventPublisher;
import com.github.losevskiyfz.candidateservice.repository.CandidateRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;
    private final CandidateEventPublisher candidateEventPublisher;

    public CandidateService(CandidateRepository candidateRepository, CandidateMapper candidateMapper, CandidateEventPublisher candidateEventPublisher) {
        this.candidateRepository = candidateRepository;
        this.candidateMapper = candidateMapper;
        this.candidateEventPublisher = candidateEventPublisher;
    }

    public CandidateResponse create(CandidateRequest request) {
        Candidate entity = candidateMapper.toEntity(request);
        Candidate savedEntity = candidateRepository.save(entity);
        candidateEventPublisher.publishCandidateCreated(savedEntity);
        return candidateMapper.toResponse(savedEntity);
    }

    public CandidateResponse getById(UUID id) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(id);
        Candidate candidate = candidateOpt.orElseThrow(() -> new CandidateNotFoundException(id));
        CandidateResponse candidateResponse = candidateMapper.toResponse(candidate);
        return candidateResponse;
    }
}
