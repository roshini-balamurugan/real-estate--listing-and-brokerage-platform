package com.realestate.backend.controller;

import com.realestate.backend.dto.VisitRequestDTO;
import com.realestate.backend.dto.VisitResponseDTO;
import com.realestate.backend.model.User;
import com.realestate.backend.model.VisitStatus;
import com.realestate.backend.service.AuthService;
import com.realestate.backend.service.VisitService;
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
@RequestMapping("/api/visits")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VisitController {

    @Autowired
    private VisitService visitService;

    @Autowired
    private AuthService authService;

    @PostMapping
    public ResponseEntity<?> scheduleVisit(
            @Valid @RequestBody VisitRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User buyer = authService.getUserByEmail(userDetails.getUsername());
            VisitResponseDTO visit = visitService.scheduleVisit(dto, buyer);
            return ResponseEntity.ok(visit);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<VisitResponseDTO>> getMyVisits(@AuthenticationPrincipal UserDetails userDetails) {
        User buyer = authService.getUserByEmail(userDetails.getUsername());
        List<VisitResponseDTO> visits = visitService.getBuyerVisits(buyer);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/host")
    public ResponseEntity<List<VisitResponseDTO>> getHostVisits(@AuthenticationPrincipal UserDetails userDetails) {
        User host = authService.getUserByEmail(userDetails.getUsername());
        List<VisitResponseDTO> visits = visitService.getHostVisits(host);
        return ResponseEntity.ok(visits);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateVisitStatus(
            @PathVariable Long id,
            @RequestParam VisitStatus status,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            VisitResponseDTO updated = visitService.updateVisitStatus(id, status, user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }
}
