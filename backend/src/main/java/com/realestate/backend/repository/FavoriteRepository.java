package com.realestate.backend.repository;

import com.realestate.backend.model.Favorite;
import com.realestate.backend.model.Property;
import com.realestate.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserOrderByCreatedAtDesc(User user);
    Optional<Favorite> findByUserAndProperty(User user, Property property);
    boolean existsByUserAndProperty(User user, Property property);
    void deleteByUserAndProperty(User user, Property property);
    void deleteByProperty(Property property);
    void deleteByUser(User user);
    void deleteByPropertyIn(List<Property> properties);
}
