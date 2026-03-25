package com.github.losevskiyfz.candidateservice.dto;

import com.github.losevskiyfz.candidateservice.entity.Grade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CandidateRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @NotNull
        Grade grade,

        @NotNull
        @PositiveOrZero
        Integer experienceYears,

        @NotNull
        @PositiveOrZero
        BigDecimal salary
) {
}
