package com.github.losevskiyfz.candidateservice.service;

import com.github.losevskiyfz.candidateservice.dto.CandidateRequest;
import com.github.losevskiyfz.candidateservice.dto.CandidateResponse;
import com.github.losevskiyfz.candidateservice.entity.Candidate;
import com.github.losevskiyfz.candidateservice.exception.CandidateNotFoundException;
import com.github.losevskiyfz.candidateservice.mapper.CandidateMapper;
import com.github.losevskiyfz.candidateservice.repository.CandidateRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;
    private final CandidateMapper candidateMapper;

    public CandidateService(CandidateRepository candidateRepository, CandidateMapper candidateMapper) {
        this.candidateRepository = candidateRepository;
        this.candidateMapper = candidateMapper;
    }

    public CandidateResponse create(CandidateRequest request) {
        Candidate entity = candidateMapper.toEntity(request);
        Candidate savedEntity = candidateRepository.save(entity);
        return candidateMapper.toResponse(savedEntity);
    }

    public CandidateResponse getById(UUID id) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(id);
        Candidate candidate = candidateOpt.orElseThrow(() -> new CandidateNotFoundException(id));
        CandidateResponse candidateResponse = candidateMapper.toResponse(candidate);
        return candidateResponse;
    }
}
