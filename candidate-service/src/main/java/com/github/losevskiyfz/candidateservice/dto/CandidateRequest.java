package com.github.losevskiyfz.candidateservice.dto;

import com.github.losevskiyfz.candidateservice.entity.Grade;

import java.math.BigDecimal;

public record CandidateRequest(
        String name,
        Grade grade,
        Integer experienceYears,
        BigDecimal salary
) {
}
