package com.realestate.backend.controller;

import com.realestate.backend.dto.PropertyRequestDTO;
import com.realestate.backend.dto.PropertyResponseDTO;
import com.realestate.backend.model.ListingType;
import com.realestate.backend.model.PropertyStatus;
import com.realestate.backend.model.PropertyType;
import com.realestate.backend.model.User;
import com.realestate.backend.service.AuthService;
import com.realestate.backend.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<PropertyResponseDTO>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) ListingType listingType,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) PropertyStatus status,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = null;
        if (userDetails != null) {
            currentUser = authService.getUserByEmail(userDetails.getUsername());
        }

        List<PropertyResponseDTO> properties = propertyService.searchProperties(
                city, minPrice, maxPrice, listingType, propertyType, status, currentUser
        );
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPropertyById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = null;
        if (userDetails != null) {
            currentUser = authService.getUserByEmail(userDetails.getUsername());
        }
        try {
            PropertyResponseDTO property = propertyService.getPropertyById(id, currentUser);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<?> createProperty(
            @Valid @RequestBody PropertyRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User currentUser = authService.getUserByEmail(userDetails.getUsername());
            PropertyResponseDTO created = propertyService.createProperty(dto, currentUser);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User currentUser = authService.getUserByEmail(userDetails.getUsername());
            PropertyResponseDTO updated = propertyService.updateProperty(id, dto, currentUser);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteProperty(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User currentUser = authService.getUserByEmail(userDetails.getUsername());
            propertyService.deleteProperty(id, currentUser);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Property successfully deleted.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER', 'ROLE_AGENT', 'ROLE_ADMIN')")
    public ResponseEntity<?> getMyProperties(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = authService.getUserByEmail(userDetails.getUsername());
        List<PropertyResponseDTO> properties = propertyService.getMyProperties(currentUser);
        return ResponseEntity.ok(properties);
    }
}
