package com.realestate.backend.dto;

import jakarta.validation.constraints.NotNull;

public class AgentVerificationDTO {

    @NotNull(message = "Verification status is required")
    private Boolean verified;

    private String adminComment;

    public AgentVerificationDTO() {
    }

    public AgentVerificationDTO(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }
}
