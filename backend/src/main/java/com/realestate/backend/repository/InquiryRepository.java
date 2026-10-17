package com.realestate.backend.repository;

import com.realestate.backend.model.Inquiry;
import com.realestate.backend.model.InquiryStatus;
import com.realestate.backend.model.Property;
import com.realestate.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findBySenderOrderByCreatedAtDesc(User sender);
    List<Inquiry> findByReceiverOrderByCreatedAtDesc(User receiver);
    List<Inquiry> findByPropertyOrderByCreatedAtDesc(Property property);
    long countByReceiverAndStatus(User receiver, InquiryStatus status);
    void deleteByProperty(Property property);
    void deleteBySender(User sender);
    void deleteByReceiver(User receiver);
    void deleteByPropertyIn(List<Property> properties);
}
