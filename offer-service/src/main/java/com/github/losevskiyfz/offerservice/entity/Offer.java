package com.github.losevskiyfz.offerservice.entity;

import com.github.losevskiyfz.offerservice.event.Grade;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Document(collection = "offers")
public class Offer {

    @Id
    private String id;

    @Indexed
    private UUID candidateId;

    private String candidateName;
    private Grade grade;
    private Integer experienceYears;
    private BigDecimal originalSalary;
    private BigDecimal recommendedSalary;
    private BigDecimal experienceCoefficient;
    private BigDecimal gradeCoefficient;
    private BigDecimal marketCoefficient;

    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public BigDecimal getOriginalSalary() {
        return originalSalary;
    }

    public void setOriginalSalary(BigDecimal originalSalary) {
        this.originalSalary = originalSalary;
    }

    public BigDecimal getRecommendedSalary() {
        return recommendedSalary;
    }

    public void setRecommendedSalary(BigDecimal recommendedSalary) {
        this.recommendedSalary = recommendedSalary;
    }

    public BigDecimal getExperienceCoefficient() {
        return experienceCoefficient;
    }

    public void setExperienceCoefficient(BigDecimal experienceCoefficient) {
        this.experienceCoefficient = experienceCoefficient;
    }

    public BigDecimal getGradeCoefficient() {
        return gradeCoefficient;
    }

    public void setGradeCoefficient(BigDecimal gradeCoefficient) {
        this.gradeCoefficient = gradeCoefficient;
    }

    public BigDecimal getMarketCoefficient() {
        return marketCoefficient;
    }

    public void setMarketCoefficient(BigDecimal marketCoefficient) {
        this.marketCoefficient = marketCoefficient;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}