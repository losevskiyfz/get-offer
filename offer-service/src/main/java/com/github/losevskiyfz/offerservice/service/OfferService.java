package com.github.losevskiyfz.offerservice.service;

import com.github.losevskiyfz.offerservice.entity.Offer;
import com.github.losevskiyfz.offerservice.event.CalculationCompletedEvent;
import com.github.losevskiyfz.offerservice.exception.OfferNotFoundException;
import com.github.losevskiyfz.offerservice.mapper.OfferMapper;
import com.github.losevskiyfz.offerservice.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class OfferService {
    private static final Logger log = LoggerFactory.getLogger(OfferService.class);

    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;

    public OfferService(OfferRepository offerRepository, OfferMapper offerMapper) {
        this.offerRepository = offerRepository;
        this.offerMapper = offerMapper;
    }

    public Offer createOffer(CalculationCompletedEvent event) {
        log.debug("Creating offer for candidateId={}", event.candidateId());
        Offer offer = offerMapper.toOffer(event);
        offer.setCreatedAt(Instant.now());
        Offer saved = offerRepository.save(offer);
        log.debug("Offer saved: id={}, candidateId={}", saved.getId(), saved.getCandidateId());
        return saved;
    }

    public Page<Offer> getOffers(Pageable pageable) {
        log.debug("Fetching offers: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return offerRepository.findAll(pageable);
    }

    public Offer getLatestOffer(UUID candidateId) {
        log.debug("Fetching latest offer for candidateId={}", candidateId);
        return offerRepository.findTopByCandidateIdOrderByCreatedAtDesc(candidateId)
                .orElseThrow(() -> new OfferNotFoundException(candidateId));
    }
}