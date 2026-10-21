package com.realestate.backend.controller;

import com.realestate.backend.dto.AgentVerificationDTO;
import com.realestate.backend.dto.DashboardStatsDTO;
import com.realestate.backend.dto.PropertyResponseDTO;
import com.realestate.backend.dto.UserDTO;
import com.realestate.backend.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            Map<String, String> res = new HashMap<>();
            res.put("message", "User deleted successfully");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/agents/pending")
    public ResponseEntity<List<UserDTO>> getPendingAgents() {
        return ResponseEntity.ok(adminService.getPendingAgents());
    }

    @PostMapping("/agents/{id}/verify")
    public ResponseEntity<?> verifyAgent(
            @PathVariable Long id,
            @Valid @RequestBody AgentVerificationDTO dto
    ) {
        try {
            UserDTO updatedAgent = adminService.verifyAgent(id, dto);
            return ResponseEntity.ok(updatedAgent);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/properties")
    public ResponseEntity<List<PropertyResponseDTO>> getAllProperties() {
        return ResponseEntity.ok(adminService.getAllPropertiesAdmin());
    }
}
