package com.realestate.backend.service;

import com.realestate.backend.dto.VisitRequestDTO;
import com.realestate.backend.dto.VisitResponseDTO;
import com.realestate.backend.model.*;
import com.realestate.backend.repository.PropertyRepository;
import com.realestate.backend.repository.VisitScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitService {

    @Autowired
    private VisitScheduleRepository visitScheduleRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public VisitResponseDTO scheduleVisit(VisitRequestDTO dto, User buyer) {
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + dto.getPropertyId()));

        VisitSchedule visit = new VisitSchedule();
        visit.setProperty(property);
        visit.setBuyer(buyer);
        visit.setVisitDate(dto.getVisitDate());
        visit.setTimeSlot(dto.getTimeSlot());
        visit.setStatus(VisitStatus.PENDING);
        visit.setNotes(dto.getNotes());

        VisitSchedule saved = visitScheduleRepository.save(visit);

        // Notify property owner
        notificationService.createNotification(
                property.getOwner(),
                "New Visit Request",
                buyer.getName() + " requested to visit '" + property.getTitle() + "' on " + dto.getVisitDate() + " (" + dto.getTimeSlot() + ").",
                "VISIT"
        );

        // Also notify agent if assigned
        if (property.getAgent() != null && !property.getAgent().getId().equals(property.getOwner().getId())) {
            notificationService.createNotification(
                    property.getAgent(),
                    "New Visit Request",
                    buyer.getName() + " requested to visit '" + property.getTitle() + "' on " + dto.getVisitDate() + " (" + dto.getTimeSlot() + ").",
                    "VISIT"
            );
        }

        // Notify buyer of successful booking request
        notificationService.createNotification(
                buyer,
                "Visit Scheduled (Pending Confirmation)",
                "Your visit request for '" + property.getTitle() + "' on " + dto.getVisitDate() + " has been submitted.",
                "VISIT"
        );

        return VisitResponseDTO.fromVisit(saved);
    }

    @Transactional
    public VisitResponseDTO updateVisitStatus(Long visitId, VisitStatus newStatus, User user) {
        VisitSchedule visit = visitScheduleRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit schedule not found with id: " + visitId));

        boolean isBuyer = visit.getBuyer().getId().equals(user.getId());
        boolean isOwner = visit.getProperty().getOwner().getId().equals(user.getId());
        boolean isAgent = visit.getProperty().getAgent() != null && visit.getProperty().getAgent().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (isBuyer) {
            // Buyer can only cancel their own visit
            if (newStatus != VisitStatus.CANCELLED) {
                throw new RuntimeException("Buyers can only cancel their visits.");
            }
        } else if (!isOwner && !isAgent && !isAdmin) {
            throw new RuntimeException("Unauthorized: You do not have permission to manage this visit schedule.");
        }

        visit.setStatus(newStatus);
        VisitSchedule updated = visitScheduleRepository.save(visit);

        // Notify buyer regarding status change
        String notifyTitle = "Visit " + newStatus;
        String notifyBody = "Your visit to '" + visit.getProperty().getTitle() + "' on " + visit.getVisitDate() + " is now " + newStatus + ".";
        notificationService.createNotification(visit.getBuyer(), notifyTitle, notifyBody, "VISIT");

        return VisitResponseDTO.fromVisit(updated);
    }

    public List<VisitResponseDTO> getBuyerVisits(User buyer) {
        return visitScheduleRepository.findByBuyerOrderByCreatedAtDesc(buyer)
                .stream()
                .map(VisitResponseDTO::fromVisit)
                .collect(Collectors.toList());
    }

    public List<VisitResponseDTO> getHostVisits(User host) {
        return visitScheduleRepository.findByHostOrderByCreatedAtDesc(host)
                .stream()
                .map(VisitResponseDTO::fromVisit)
                .collect(Collectors.toList());
    }
}
