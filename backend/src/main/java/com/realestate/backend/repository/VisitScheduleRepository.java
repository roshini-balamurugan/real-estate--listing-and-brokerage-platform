package com.realestate.backend.repository;

import com.realestate.backend.model.Property;
import com.realestate.backend.model.User;
import com.realestate.backend.model.VisitSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitScheduleRepository extends JpaRepository<VisitSchedule, Long> {

    List<VisitSchedule> findByBuyerOrderByCreatedAtDesc(User buyer);

    @Query("SELECT v FROM VisitSchedule v WHERE v.property.owner = :user OR v.property.agent = :user ORDER BY v.createdAt DESC")
    List<VisitSchedule> findByHostOrderByCreatedAtDesc(@Param("user") User user);

    List<VisitSchedule> findByPropertyOrderByCreatedAtDesc(Property property);
    
    void deleteByProperty(Property property);

    void deleteByBuyer(User buyer);

    void deleteByPropertyIn(List<Property> properties);
}
