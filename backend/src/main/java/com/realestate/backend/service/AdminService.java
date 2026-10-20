package com.realestate.backend.service;

import com.realestate.backend.dto.AgentVerificationDTO;
import com.realestate.backend.dto.DashboardStatsDTO;
import com.realestate.backend.dto.PropertyResponseDTO;
import com.realestate.backend.dto.UserDTO;
import com.realestate.backend.model.*;
import com.realestate.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private VisitScheduleRepository visitScheduleRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getPendingAgents() {
        return userRepository.findByAgentVerifiedFalseAndRole(Role.ROLE_AGENT)
                .stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getVerifiedAgents() {
        return userRepository.findByRoleAndAgentVerified(Role.ROLE_AGENT, true)
                .stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO verifyAgent(Long agentId, AgentVerificationDTO dto) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found with id: " + agentId));

        if (agent.getRole() != Role.ROLE_AGENT) {
            throw new RuntimeException("User is not an agent.");
        }

        agent.setAgentVerified(dto.getVerified());
        User saved = userRepository.save(agent);

        String title = dto.getVerified() ? "Agent License Approved!" : "Agent License Rejected";
        String message = dto.getVerified()
                ? "Congratulations! Your broker/agent license has been verified by the administrator. You can now publish properties."
                : "Your agent verification was not approved. " + (dto.getAdminComment() != null ? dto.getAdminComment() : "Please contact support for further details.");

        notificationService.createNotification(agent, title, message, "VERIFICATION");

        return UserDTO.fromUser(saved);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new RuntimeException("Admin users cannot be deleted.");
        }

        // 1. Delete notifications received by the user
        notificationRepository.deleteByRecipient(user);

        // 2. Delete favorites saved by user
        favoriteRepository.deleteByUser(user);

        // 3. Delete visit schedules requested by user
        visitScheduleRepository.deleteByBuyer(user);

        // 4. Delete inquiries sent or received by user
        inquiryRepository.deleteBySender(user);
        inquiryRepository.deleteByReceiver(user);

        // 5. Unassign user as agent from other properties if applicable
        List<Property> agentProperties = propertyRepository.findByAgent(user);
        for (Property ap : agentProperties) {
            if (!ap.getOwner().getId().equals(user.getId())) {
                ap.setAgent(null);
                propertyRepository.save(ap);
            }
        }

        // 6. Delete properties owned by user and their associated relations
        List<Property> ownedProperties = propertyRepository.findByOwner(user);
        if (!ownedProperties.isEmpty()) {
            favoriteRepository.deleteByPropertyIn(ownedProperties);
            visitScheduleRepository.deleteByPropertyIn(ownedProperties);
            inquiryRepository.deleteByPropertyIn(ownedProperties);
            propertyRepository.deleteAll(ownedProperties);
        }

        // 7. Finally delete the user
        userRepository.delete(user);
    }

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        stats.setTotalUsers(userRepository.count());
        stats.setTotalBuyers(userRepository.countByRole(Role.ROLE_BUYER));
        stats.setTotalSellers(userRepository.countByRole(Role.ROLE_SELLER));
        stats.setTotalAgents(userRepository.countByRole(Role.ROLE_AGENT));
        stats.setPendingAgentVerifications(userRepository.findByAgentVerifiedFalseAndRole(Role.ROLE_AGENT).size());

        stats.setTotalProperties(propertyRepository.count());
        stats.setPropertiesForSale(propertyRepository.countByListingType(ListingType.SALE));
        stats.setPropertiesForRent(propertyRepository.countByListingType(ListingType.RENT));
        stats.setActiveListings(propertyRepository.countByStatus(PropertyStatus.AVAILABLE));

        stats.setTotalVisits(visitScheduleRepository.count());
        stats.setTotalInquiries(inquiryRepository.count());

        return stats;
    }

    public List<PropertyResponseDTO> getAllPropertiesAdmin() {
        return propertyRepository.findAll()
                .stream()
                .map(PropertyResponseDTO::fromProperty)
                .collect(Collectors.toList());
    }
}
