package com.github.losevskiyfz.offerservice.controller;

import com.github.losevskiyfz.offerservice.entity.Offer;
import com.github.losevskiyfz.offerservice.service.OfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(OfferController.BASE_URL)
public class OfferController implements OfferApi {
    private static final Logger log = LoggerFactory.getLogger(OfferController.class);
    static final String BASE_URL = "/api/v1/offers";

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public ResponseEntity<Page<Offer>> getOffers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        log.info("GET {}: page={}, size={}", BASE_URL, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(offerService.getOffers(pageable));
    }

    @GetMapping("/candidates/{candidateId}")
    public ResponseEntity<Offer> getLatestOffer(@PathVariable UUID candidateId) {
        log.info("GET {}/candidates/{}", BASE_URL, candidateId);
        return ResponseEntity.ok(offerService.getLatestOffer(candidateId));
    }
}