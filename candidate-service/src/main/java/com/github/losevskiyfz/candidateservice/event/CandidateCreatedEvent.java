package com.github.losevskiyfz.candidateservice.event;

import com.github.losevskiyfz.candidateservice.entity.Grade;

import java.math.BigDecimal;
import java.util.UUID;

public record CandidateCreatedEvent(
        UUID id,
        String name,
        Grade grade,
        Integer experienceYears,
        BigDecimal salary
) {}