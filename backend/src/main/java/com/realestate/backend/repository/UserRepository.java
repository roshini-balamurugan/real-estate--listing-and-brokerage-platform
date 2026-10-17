package com.realestate.backend.repository;

import com.realestate.backend.model.Role;
import com.realestate.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByRoleAndAgentVerified(Role role, Boolean agentVerified);
    List<User> findByAgentVerifiedFalseAndRole(Role role);
    long countByRole(Role role);
}
