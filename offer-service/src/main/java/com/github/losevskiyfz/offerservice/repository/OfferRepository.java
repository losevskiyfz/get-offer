package com.github.losevskiyfz.offerservice.repository;

import com.github.losevskiyfz.offerservice.entity.Offer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface OfferRepository extends MongoRepository<Offer, String> {

    Optional<Offer> findTopByCandidateIdOrderByCreatedAtDesc(UUID candidateId);
}