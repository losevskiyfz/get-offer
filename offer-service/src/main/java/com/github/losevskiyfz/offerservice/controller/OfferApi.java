package com.github.losevskiyfz.offerservice.controller;

import com.github.losevskiyfz.offerservice.entity.Offer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "Offers", description = "API for managing offers")
public interface OfferApi {

    @Operation(summary = "Get all offers")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Offers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    ResponseEntity<Page<Offer>> getOffers(Pageable pageable);

    @Operation(summary = "Get latest offer by candidate ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Offer found",
                    content = @Content(schema = @Schema(implementation = Offer.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Offer not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid UUID format",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<Offer> getLatestOffer(
            @Parameter(description = "Candidate UUID") @PathVariable UUID candidateId
    );
}