package com.realestate.backend.controller;

import com.realestate.backend.dto.UserDTO;
import com.realestate.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AgentController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getVerifiedAgents() {
        return ResponseEntity.ok(adminService.getVerifiedAgents());
    }
}
