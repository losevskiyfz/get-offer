package com.github.losevskiyfz.candidateservice.dto;

import com.github.losevskiyfz.candidateservice.entity.Grade;

import java.math.BigDecimal;
import java.util.UUID;

public record CandidateResponse(
        UUID id,
        String name,
        Grade grade,
        Integer experienceYears,
        BigDecimal salary
) {
}
