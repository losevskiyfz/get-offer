package com.github.losevskiyfz.candidateservice.controller;

import com.github.losevskiyfz.candidateservice.dto.CandidateRequest;
import com.github.losevskiyfz.candidateservice.dto.CandidateResponse;
import com.github.losevskiyfz.candidateservice.service.CandidateService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(CandidateController.BASE_PATH)
public class CandidateController implements CandidateApi {

    private static final Logger log = LoggerFactory.getLogger(CandidateController.class);
    public static final String BASE_PATH = "/api/v1/candidates";

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    @Override
    public ResponseEntity<CandidateResponse> saveCandidate(@Valid @RequestBody CandidateRequest requestBody) {
        log.debug("{} - saving candidate: name={}, grade={}", BASE_PATH, requestBody.name(), requestBody.grade());
        CandidateResponse responseBody = candidateService.create(requestBody);
        log.debug("{} - candidate saved: id={}", BASE_PATH, responseBody.id());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseBody.id())
                .toUri();
        return ResponseEntity.created(location).body(responseBody);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<CandidateResponse> getCandidate(@PathVariable UUID id) {
        log.debug("{}/{} - fetching candidate", BASE_PATH, id);
        CandidateResponse responseBody = candidateService.getById(id);
        log.debug("{}/{} - candidate found: name={}", BASE_PATH, id, responseBody.name());
        return ResponseEntity.ok(responseBody);
    }
}