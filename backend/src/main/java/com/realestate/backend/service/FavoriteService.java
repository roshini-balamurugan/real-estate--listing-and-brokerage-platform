package com.realestate.backend.service;

import com.realestate.backend.dto.PropertyResponseDTO;
import com.realestate.backend.model.Favorite;
import com.realestate.backend.model.Property;
import com.realestate.backend.model.User;
import com.realestate.backend.repository.FavoriteRepository;
import com.realestate.backend.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Transactional
    public void addToFavorites(Long propertyId, User user) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));

        if (!favoriteRepository.existsByUserAndProperty(user, property)) {
            Favorite favorite = new Favorite(user, property);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void removeFromFavorites(Long propertyId, User user) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId));

        favoriteRepository.deleteByUserAndProperty(user, property);
    }

    public List<PropertyResponseDTO> getUserFavorites(User user) {
        return favoriteRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(fav -> {
                    PropertyResponseDTO dto = PropertyResponseDTO.fromProperty(fav.getProperty());
                    dto.setIsFavorite(true);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public boolean isFavorite(Long propertyId, User user) {
        if (user == null) return false;
        Property property = propertyRepository.findById(propertyId).orElse(null);
        if (property == null) return false;
        return favoriteRepository.existsByUserAndProperty(user, property);
    }
}
