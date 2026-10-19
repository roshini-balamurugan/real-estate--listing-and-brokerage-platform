package com.realestate.backend.dto;

import com.realestate.backend.model.VisitSchedule;
import com.realestate.backend.model.VisitStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VisitResponseDTO {

    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private String propertyCity;
    private Double propertyPrice;
    private String propertyImageUrl;
    private String propertyAddress;

    private UserDTO buyer;
    private UserDTO host; // Either agent or owner

    private LocalDate visitDate;
    private String timeSlot;
    private VisitStatus status;
    private String notes;
    private LocalDateTime createdAt;

    public VisitResponseDTO() {
    }

    public static VisitResponseDTO fromVisit(VisitSchedule visit) {
        if (visit == null) return null;
        VisitResponseDTO dto = new VisitResponseDTO();
        dto.setId(visit.getId());
        if (visit.getProperty() != null) {
            dto.setPropertyId(visit.getProperty().getId());
            dto.setPropertyTitle(visit.getProperty().getTitle());
            dto.setPropertyCity(visit.getProperty().getCity());
            dto.setPropertyPrice(visit.getProperty().getPrice());
            dto.setPropertyImageUrl(visit.getProperty().getImageUrl());
            dto.setPropertyAddress(visit.getProperty().getAddress());
            // Host is agent if present, otherwise owner
            if (visit.getProperty().getAgent() != null) {
                dto.setHost(UserDTO.fromUser(visit.getProperty().getAgent()));
            } else {
                dto.setHost(UserDTO.fromUser(visit.getProperty().getOwner()));
            }
        }
        dto.setBuyer(UserDTO.fromUser(visit.getBuyer()));
        dto.setVisitDate(visit.getVisitDate());
        dto.setTimeSlot(visit.getTimeSlot());
        dto.setStatus(visit.getStatus());
        dto.setNotes(visit.getNotes());
        dto.setCreatedAt(visit.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public String getPropertyCity() {
        return propertyCity;
    }

    public void setPropertyCity(String propertyCity) {
        this.propertyCity = propertyCity;
    }

    public Double getPropertyPrice() {
        return propertyPrice;
    }

    public void setPropertyPrice(Double propertyPrice) {
        this.propertyPrice = propertyPrice;
    }

    public String getPropertyImageUrl() {
        return propertyImageUrl;
    }

    public void setPropertyImageUrl(String propertyImageUrl) {
        this.propertyImageUrl = propertyImageUrl;
    }

    public String getPropertyAddress() {
        return propertyAddress;
    }

    public void setPropertyAddress(String propertyAddress) {
        this.propertyAddress = propertyAddress;
    }

    public UserDTO getBuyer() {
        return buyer;
    }

    public void setBuyer(UserDTO buyer) {
        this.buyer = buyer;
    }

    public UserDTO getHost() {
        return host;
    }

    public void setHost(UserDTO host) {
        this.host = host;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public VisitStatus getStatus() {
        return status;
    }

    public void setStatus(VisitStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
