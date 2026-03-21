package com.github.losevskiyfz.candidateservice.mapper;

import com.github.losevskiyfz.candidateservice.dto.CandidateRequest;
import com.github.losevskiyfz.candidateservice.dto.CandidateResponse;
import com.github.losevskiyfz.candidateservice.entity.Candidate;
import com.github.losevskiyfz.candidateservice.event.CandidateCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CandidateMapper {
    @Mapping(target = "id", ignore = true)
    Candidate toEntity(CandidateRequest request);

    CandidateResponse toResponse(Candidate candidate);

    CandidateCreatedEvent toCreatedEvent(Candidate candidate);
}