package com.realestate.backend.service;

import com.realestate.backend.dto.InquiryRequestDTO;
import com.realestate.backend.dto.InquiryResponseDTO;
import com.realestate.backend.model.*;
import com.realestate.backend.repository.InquiryRepository;
import com.realestate.backend.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public InquiryResponseDTO sendInquiry(InquiryRequestDTO dto, User sender) {
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + dto.getPropertyId()));

        // Receiver is agent if assigned, else owner
        User receiver = property.getAgent() != null ? property.getAgent() : property.getOwner();

        Inquiry inquiry = new Inquiry();
        inquiry.setProperty(property);
        inquiry.setSender(sender);
        inquiry.setReceiver(receiver);
        inquiry.setSubject(dto.getSubject());
        inquiry.setMessage(dto.getMessage());
        inquiry.setPhone(dto.getPhone() != null ? dto.getPhone() : sender.getPhone());
        inquiry.setStatus(InquiryStatus.NEW);

        Inquiry saved = inquiryRepository.save(inquiry);

        // Send notification to recipient
        notificationService.createNotification(
                receiver,
                "New Property Inquiry: " + dto.getSubject(),
                "Inquiry from " + sender.getName() + " for '" + property.getTitle() + "': " + dto.getMessage(),
                "INQUIRY"
        );

        return InquiryResponseDTO.fromInquiry(saved);
    }

    public List<InquiryResponseDTO> getSentInquiries(User sender) {
        return inquiryRepository.findBySenderOrderByCreatedAtDesc(sender)
                .stream()
                .map(InquiryResponseDTO::fromInquiry)
                .collect(Collectors.toList());
    }

    public List<InquiryResponseDTO> getReceivedInquiries(User receiver) {
        return inquiryRepository.findByReceiverOrderByCreatedAtDesc(receiver)
                .stream()
                .map(InquiryResponseDTO::fromInquiry)
                .collect(Collectors.toList());
    }

    @Transactional
    public InquiryResponseDTO updateStatus(Long inquiryId, InquiryStatus status, User user) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with id: " + inquiryId));

        if (!inquiry.getReceiver().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Unauthorized: You can only update inquiries addressed to you.");
        }

        inquiry.setStatus(status);
        Inquiry updated = inquiryRepository.save(inquiry);
        return InquiryResponseDTO.fromInquiry(updated);
    }
}
