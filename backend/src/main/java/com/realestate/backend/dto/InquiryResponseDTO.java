package com.realestate.backend.dto;

import com.realestate.backend.model.Inquiry;
import com.realestate.backend.model.InquiryStatus;

import java.time.LocalDateTime;

public class InquiryResponseDTO {

    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private String propertyCity;
    private Double propertyPrice;
    private String propertyImageUrl;

    private UserDTO sender;
    private UserDTO receiver;

    private String subject;
    private String message;
    private String phone;
    private InquiryStatus status;
    private LocalDateTime createdAt;

    public InquiryResponseDTO() {
    }

    public static InquiryResponseDTO fromInquiry(Inquiry inquiry) {
        if (inquiry == null) return null;
        InquiryResponseDTO dto = new InquiryResponseDTO();
        dto.setId(inquiry.getId());
        if (inquiry.getProperty() != null) {
            dto.setPropertyId(inquiry.getProperty().getId());
            dto.setPropertyTitle(inquiry.getProperty().getTitle());
            dto.setPropertyCity(inquiry.getProperty().getCity());
            dto.setPropertyPrice(inquiry.getProperty().getPrice());
            dto.setPropertyImageUrl(inquiry.getProperty().getImageUrl());
        }
        dto.setSender(UserDTO.fromUser(inquiry.getSender()));
        dto.setReceiver(UserDTO.fromUser(inquiry.getReceiver()));
        dto.setSubject(inquiry.getSubject());
        dto.setMessage(inquiry.getMessage());
        dto.setPhone(inquiry.getPhone());
        dto.setStatus(inquiry.getStatus());
        dto.setCreatedAt(inquiry.getCreatedAt());
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

    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    public UserDTO getReceiver() {
        return receiver;
    }

    public void setReceiver(UserDTO receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
