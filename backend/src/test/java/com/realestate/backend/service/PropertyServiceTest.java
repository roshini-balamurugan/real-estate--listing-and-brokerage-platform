package com.realestate.backend.service;

import com.realestate.backend.dto.PropertyRequestDTO;
import com.realestate.backend.dto.PropertyResponseDTO;
import com.realestate.backend.model.*;
import com.realestate.backend.repository.FavoriteRepository;
import com.realestate.backend.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PropertyService propertyService;

    private User seller;
    private Property property;

    @BeforeEach
    void setUp() {
        seller = new User("David Miller", "seller@example.com", "pass", "+1 555-1122", Role.ROLE_SELLER);
        seller.setId(1L);

        property = new Property();
        property.setId(100L);
        property.setTitle("Modern Condo in Downtown");
        property.setCity("Seattle");
        property.setPrice(650000.0);
        property.setPropertyType(PropertyType.APARTMENT);
        property.setListingType(ListingType.SALE);
        property.setStatus(PropertyStatus.AVAILABLE);
        property.setOwner(seller);
    }

    @Test
    void testGetPropertyByIdFound() {
        when(propertyRepository.findById(100L)).thenReturn(Optional.of(property));
        when(favoriteRepository.existsByUserAndProperty(any(), any())).thenReturn(false);

        PropertyResponseDTO dto = propertyService.getPropertyById(100L, seller);

        assertNotNull(dto);
        assertEquals("Modern Condo in Downtown", dto.getTitle());
        assertEquals("Seattle", dto.getCity());
    }

    @Test
    void testCreatePropertyBySellerSuccess() {
        PropertyRequestDTO request = new PropertyRequestDTO();
        request.setTitle("Modern Condo in Downtown");
        request.setCity("Seattle");
        request.setPrice(650000.0);
        request.setPropertyType(PropertyType.APARTMENT);
        request.setListingType(ListingType.SALE);

        when(propertyRepository.save(any(Property.class))).thenReturn(property);

        PropertyResponseDTO created = propertyService.createProperty(request, seller);

        assertNotNull(created);
        assertEquals(100L, created.getId());
        verify(propertyRepository, times(1)).save(any(Property.class));
    }

    @Test
    void testCreatePropertyByBuyerFails() {
        User buyer = new User("Bob", "bob@example.com", "pass", "+1 555", Role.ROLE_BUYER);
        PropertyRequestDTO request = new PropertyRequestDTO();

        assertThrows(RuntimeException.class, () -> propertyService.createProperty(request, buyer));
        verify(propertyRepository, never()).save(any(Property.class));
    }
}
