package com.github.losevskiyfz.candidateservice.controller;

import com.github.losevskiyfz.candidateservice.dto.CandidateRequest;
import com.github.losevskiyfz.candidateservice.dto.CandidateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Candidates", description = "API for managing candidates")
public interface CandidateApi {

    @Operation(summary = "Create a candidate")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Candidate created successfully",
                    content = @Content(schema = @Schema(implementation = CandidateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<CandidateResponse> saveCandidate(CandidateRequest requestBody);

    @Operation(summary = "Get a candidate by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Candidate found",
                    content = @Content(schema = @Schema(implementation = CandidateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Candidate not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid UUID format",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<CandidateResponse> getCandidate(
            @Parameter(description = "Candidate UUID") UUID id
    );
}
