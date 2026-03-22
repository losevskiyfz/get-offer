package com.github.losevskiyfz.offerservice.exception;

import java.util.UUID;

public class OfferNotFoundException extends RuntimeException {
    public OfferNotFoundException(UUID candidateId) {
        super("Offer not found for candidateId=" + candidateId);
    }
}
