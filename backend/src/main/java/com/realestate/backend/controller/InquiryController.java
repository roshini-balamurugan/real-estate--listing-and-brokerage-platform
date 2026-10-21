package com.realestate.backend.controller;

import com.realestate.backend.dto.InquiryRequestDTO;
import com.realestate.backend.dto.InquiryResponseDTO;
import com.realestate.backend.model.InquiryStatus;
import com.realestate.backend.model.User;
import com.realestate.backend.service.AuthService;
import com.realestate.backend.service.InquiryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inquiries")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity<?> sendInquiry(
            @Valid @RequestBody InquiryRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User sender = authService.getUserByEmail(userDetails.getUsername());
            InquiryResponseDTO response = inquiryService.sendInquiry(dto, sender);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/sent")
    public ResponseEntity<List<InquiryResponseDTO>> getSentInquiries(@AuthenticationPrincipal UserDetails userDetails) {
        User sender = authService.getUserByEmail(userDetails.getUsername());
        List<InquiryResponseDTO> inquiries = inquiryService.getSentInquiries(sender);
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/received")
    public ResponseEntity<List<InquiryResponseDTO>> getReceivedInquiries(@AuthenticationPrincipal UserDetails userDetails) {
        User receiver = authService.getUserByEmail(userDetails.getUsername());
        List<InquiryResponseDTO> inquiries = inquiryService.getReceivedInquiries(receiver);
        return ResponseEntity.ok(inquiries);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateInquiryStatus(
            @PathVariable Long id,
            @RequestParam InquiryStatus status,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            InquiryResponseDTO updated = inquiryService.updateStatus(id, status, user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }
}
