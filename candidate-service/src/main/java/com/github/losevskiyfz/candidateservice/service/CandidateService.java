package com.github.losevskiyfz.candidateservice.service;

import com.github.losevskiyfz.candidateservice.dto.CandidateRequest;
import com.github.losevskiyfz.candidateservice.dto.CandidateResponse;
import com.github.losevskiyfz.candidateservice.entity.Candidate;
import com.github.losevskiyfz.candidateservice.exception.CandidateNotFoundException;
import com.github.losevskiyfz.candidateservice.mapper.CandidateMapper;
import com.github.losevskiyfz.candidateservice.messaging.CandidateEventPublisher;
import com.github.losevskiyfz.candidateservice.repository.CandidateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CandidateService {
    private static final Logger log = LoggerFactory.getLogger(CandidateService.class);

    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;
    private final CandidateEventPublisher candidateEventPublisher;

    public CandidateService(CandidateRepository candidateRepository, CandidateMapper candidateMapper, CandidateEventPublisher candidateEventPublisher) {
        this.candidateRepository = candidateRepository;
        this.candidateMapper = candidateMapper;
        this.candidateEventPublisher = candidateEventPublisher;
    }

    public CandidateResponse create(CandidateRequest request) {
        log.debug("Creating candidate: name={}, grade={}", request.name(), request.grade());
        Candidate entity = candidateMapper.toEntity(request);
        Candidate savedEntity = candidateRepository.save(entity);
        log.debug("Candidate saved: id={}", savedEntity.getId());
        candidateEventPublisher.publishCandidateCreated(savedEntity);
        return candidateMapper.toResponse(savedEntity);
    }

    public CandidateResponse getById(UUID id) {
        log.debug("Fetching candidate: id={}", id);
        Optional<Candidate> candidateOpt = candidateRepository.findById(id);
        Candidate candidate = candidateOpt.orElseThrow(() -> new CandidateNotFoundException(id));
        log.debug("Candidate found: id={}, name={}", id, candidate.getName());
        return candidateMapper.toResponse(candidate);
    }
}