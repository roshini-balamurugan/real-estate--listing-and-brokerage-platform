package com.realestate.backend.controller;

import com.realestate.backend.dto.PropertyResponseDTO;
import com.realestate.backend.model.User;
import com.realestate.backend.service.AuthService;
import com.realestate.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<PropertyResponseDTO>> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        User user = authService.getUserByEmail(userDetails.getUsername());
        List<PropertyResponseDTO> favorites = favoriteService.getUserFavorites(user);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/{propertyId}")
    public ResponseEntity<?> addFavorite(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            favoriteService.addToFavorites(propertyId, user);
            Map<String, String> res = new HashMap<>();
            res.put("message", "Property added to favorites");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> removeFavorite(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            User user = authService.getUserByEmail(userDetails.getUsername());
            favoriteService.removeFromFavorites(propertyId, user);
            Map<String, String> res = new HashMap<>();
            res.put("message", "Property removed from favorites");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        }
    }

    @GetMapping("/check/{propertyId}")
    public ResponseEntity<?> checkFavorite(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean isFav = false;
        if (userDetails != null) {
            User user = authService.getUserByEmail(userDetails.getUsername());
            isFav = favoriteService.isFavorite(propertyId, user);
        }
        Map<String, Boolean> res = new HashMap<>();
        res.put("favorite", isFav);
        return ResponseEntity.ok(res);
    }
}
