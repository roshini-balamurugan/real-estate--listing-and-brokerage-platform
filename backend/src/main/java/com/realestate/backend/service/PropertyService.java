package com.realestate.backend.service;

import com.realestate.backend.dto.PropertyRequestDTO;
import com.realestate.backend.dto.PropertyResponseDTO;
import com.realestate.backend.model.*;
import com.realestate.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private VisitScheduleRepository visitScheduleRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private NotificationService notificationService;

    public List<PropertyResponseDTO> getAllProperties(User currentUser) {
        return propertyRepository.findByStatusOrderByCreatedAtDesc(PropertyStatus.AVAILABLE)
                .stream()
                .map(p -> mapToResponseDTO(p, currentUser))
                .collect(Collectors.toList());
    }

    public List<PropertyResponseDTO> searchProperties(
            String city,
            Double minPrice,
            Double maxPrice,
            ListingType listingType,
            PropertyType propertyType,
            PropertyStatus status,
            User currentUser
    ) {
        if (city != null && city.trim().isEmpty()) city = null;
        if (status == null) status = PropertyStatus.AVAILABLE;

        return propertyRepository.searchProperties(city, minPrice, maxPrice, listingType, propertyType, status)
                .stream()
                .map(p -> mapToResponseDTO(p, currentUser))
                .collect(Collectors.toList());
    }

    public PropertyResponseDTO getPropertyById(Long id, User currentUser) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        return mapToResponseDTO(property, currentUser);
    }

    @Transactional
    public PropertyResponseDTO createProperty(PropertyRequestDTO dto, User user) {
        // Only SELLER, AGENT, ADMIN can create properties
        if (user.getRole() != Role.ROLE_SELLER && user.getRole() != Role.ROLE_AGENT && user.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Only sellers, agents, and admins can create property listings.");
        }

        // If user is AGENT, check if verified
        if (user.getRole() == Role.ROLE_AGENT && (user.getAgentVerified() == null || !user.getAgentVerified())) {
            throw new RuntimeException("Your agent account is pending verification by admin. You cannot list properties yet.");
        }

        Property property = new Property();
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setPropertyType(dto.getPropertyType());
        property.setListingType(dto.getListingType());
        property.setPrice(dto.getPrice());
        property.setCity(dto.getCity());
        property.setState(dto.getState());
        property.setAddress(dto.getAddress());
        property.setBedrooms(dto.getBedrooms());
        property.setBathrooms(dto.getBathrooms());
        property.setAreaSqFt(dto.getAreaSqFt());
        property.setStatus(dto.getStatus() != null ? dto.getStatus() : PropertyStatus.AVAILABLE);
        property.setImageUrl(dto.getImageUrl());
        property.setAdditionalImages(dto.getAdditionalImages());

        property.setOwner(user);

        // If agentId specified or user is agent
        if (user.getRole() == Role.ROLE_AGENT) {
            property.setAgent(user);
        } else if (dto.getAgentId() != null) {
            User agent = userRepository.findById(dto.getAgentId()).orElse(null);
            if (agent != null && agent.getRole() == Role.ROLE_AGENT) {
                property.setAgent(agent);
            }
        }

        Property saved = propertyRepository.save(property);

        notificationService.createNotification(
                user,
                "Listing Created",
                "Your property '" + saved.getTitle() + "' was successfully listed.",
                "SYSTEM"
        );

        return mapToResponseDTO(saved, user);
    }

    @Transactional
    public PropertyResponseDTO updateProperty(Long id, PropertyRequestDTO dto, User user) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));

        // Check ownership/role
        boolean isOwner = property.getOwner().getId().equals(user.getId());
        boolean isAssignedAgent = property.getAgent() != null && property.getAgent().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAssignedAgent && !isAdmin) {
            throw new RuntimeException("Unauthorized: You do not have permission to update this property.");
        }

        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setPropertyType(dto.getPropertyType());
        property.setListingType(dto.getListingType());
        property.setPrice(dto.getPrice());
        property.setCity(dto.getCity());
        property.setState(dto.getState());
        property.setAddress(dto.getAddress());
        property.setBedrooms(dto.getBedrooms());
        property.setBathrooms(dto.getBathrooms());
        property.setAreaSqFt(dto.getAreaSqFt());
        if (dto.getStatus() != null) {
            property.setStatus(dto.getStatus());
        }
        if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            property.setImageUrl(dto.getImageUrl());
        }
        if (dto.getAdditionalImages() != null) {
            property.setAdditionalImages(dto.getAdditionalImages());
        }

        if (dto.getAgentId() != null) {
            User agent = userRepository.findById(dto.getAgentId()).orElse(null);
            if (agent != null && agent.getRole() == Role.ROLE_AGENT) {
                property.setAgent(agent);
            }
        }

        Property updated = propertyRepository.save(property);
        return mapToResponseDTO(updated, user);
    }

    @Transactional
    public void deleteProperty(Long id, User user) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));

        boolean isOwner = property.getOwner().getId().equals(user.getId());
        boolean isAssignedAgent = property.getAgent() != null && property.getAgent().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAssignedAgent && !isAdmin) {
            throw new RuntimeException("Unauthorized: You do not have permission to delete this property.");
        }

        // Clean dependencies
        favoriteRepository.deleteByProperty(property);
        visitScheduleRepository.deleteByProperty(property);
        inquiryRepository.deleteByProperty(property);

        propertyRepository.delete(property);
    }

    public List<PropertyResponseDTO> getMyProperties(User user) {
        List<Property> properties = propertyRepository.findByOwnerOrAgentOrderByCreatedAtDesc(user);
        return properties.stream().map(p -> mapToResponseDTO(p, user)).collect(Collectors.toList());
    }

    private PropertyResponseDTO mapToResponseDTO(Property property, User currentUser) {
        PropertyResponseDTO dto = PropertyResponseDTO.fromProperty(property);
        if (currentUser != null) {
            dto.setIsFavorite(favoriteRepository.existsByUserAndProperty(currentUser, property));
        } else {
            dto.setIsFavorite(false);
        }
        return dto;
    }
}
