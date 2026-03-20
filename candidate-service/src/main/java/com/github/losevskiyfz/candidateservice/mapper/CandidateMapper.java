package com.github.losevskiyfz.candidateservice.mapper;

import com.github.losevskiyfz.candidateservice.dto.CandidateRequest;
import com.github.losevskiyfz.candidateservice.dto.CandidateResponse;
import com.github.losevskiyfz.candidateservice.entity.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CandidateMapper {

    Candidate toEntity(CandidateRequest request);

    CandidateResponse toResponse(Candidate candidate);

}