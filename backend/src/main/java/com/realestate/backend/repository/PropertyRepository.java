package com.realestate.backend.repository;

import com.realestate.backend.model.ListingType;
import com.realestate.backend.model.Property;
import com.realestate.backend.model.PropertyStatus;
import com.realestate.backend.model.PropertyType;
import com.realestate.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwnerOrderByCreatedAtDesc(User owner);

    List<Property> findByOwner(User owner);

    List<Property> findByAgent(User agent);

    List<Property> findByAgentOrderByCreatedAtDesc(User agent);

    @Query("SELECT p FROM Property p WHERE p.owner = :user OR p.agent = :user ORDER BY p.createdAt DESC")
    List<Property> findByOwnerOrAgentOrderByCreatedAtDesc(@Param("user") User user);

    List<Property> findByStatusOrderByCreatedAtDesc(PropertyStatus status);

    @Query("SELECT p FROM Property p WHERE " +
           "(:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%')) OR LOWER(p.title) LIKE LOWER(CONCAT('%', :city, '%')) OR LOWER(p.address) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:listingType IS NULL OR p.listingType = :listingType) AND " +
           "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
           "(:status IS NULL OR p.status = :status) " +
           "ORDER BY p.createdAt DESC")
    List<Property> searchProperties(
            @Param("city") String city,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("listingType") ListingType listingType,
            @Param("propertyType") PropertyType propertyType,
            @Param("status") PropertyStatus status
    );

    long countByListingType(ListingType listingType);
    long countByStatus(PropertyStatus status);
}
