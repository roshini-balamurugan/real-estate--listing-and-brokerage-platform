package com.realestate.backend.service;

import com.realestate.backend.model.*;
import com.realestate.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private VisitScheduleRepository visitScheduleRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return; // Data already exists
        }

        // 1. Create Users
        User admin = new User("Platform Admin", "admin@realestate.com", passwordEncoder.encode("admin123"), "+1 800-555-0199", Role.ROLE_ADMIN);
        admin.setAgentVerified(true);
        userRepository.save(admin);

        User agent1 = new User("Michael Ross", "agent@realestate.com", passwordEncoder.encode("agent123"), "+1 212-555-0144", Role.ROLE_AGENT);
        agent1.setAgencyName("Prime Manhattan Realty");
        agent1.setLicenseNumber("NY-RE-778899");
        agent1.setAgentVerified(true);
        userRepository.save(agent1);

        User agent2 = new User("Sarah Jenkins", "sarah.agent@realestate.com", passwordEncoder.encode("agent123"), "+1 310-555-0182", Role.ROLE_AGENT);
        agent2.setAgencyName("Pacific Coast Estates");
        agent2.setLicenseNumber("CA-BR-554433");
        agent2.setAgentVerified(false); // Pending verification!
        userRepository.save(agent2);

        User seller = new User("David Miller", "seller@realestate.com", passwordEncoder.encode("seller123"), "+1 786-555-0123", Role.ROLE_SELLER);
        userRepository.save(seller);

        User buyer = new User("Emma Watson", "buyer@realestate.com", passwordEncoder.encode("buyer123"), "+1 415-555-0177", Role.ROLE_BUYER);
        userRepository.save(buyer);

        // 2. Create Sample Properties
        Property p1 = new Property();
        p1.setTitle("Luxury Waterfront Villa with Private Pool");
        p1.setDescription("Stunning contemporary villa overlooking Biscayne Bay. Features floor-to-ceiling glass walls, a private infinity pool, chef's kitchen with Sub-Zero appliances, smart home automation, and private yacht dockage.");
        p1.setPropertyType(PropertyType.VILLA);
        p1.setListingType(ListingType.SALE);
        p1.setPrice(1450000.0);
        p1.setCity("Miami");
        p1.setState("Florida");
        p1.setAddress("442 Ocean Drive, Star Island");
        p1.setBedrooms(5);
        p1.setBathrooms(6);
        p1.setAreaSqFt(5800.0);
        p1.setStatus(PropertyStatus.AVAILABLE);
        p1.setImageUrl("https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&w=1200&q=80");
        p1.setAdditionalImages("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=800&q=80");
        p1.setOwner(seller);
        p1.setAgent(agent1);
        propertyRepository.save(p1);

        Property p2 = new Property();
        p2.setTitle("Modern High-Rise Penthouse with Skyline Views");
        p2.setDescription("Ultra-luxury penthouse on the 48th floor with panoramic 360-degree views of Manhattan. Custom Italian marble finishes, private elevator access, 12ft ceilings, and 24/7 white-glove concierge service.");
        p2.setPropertyType(PropertyType.APARTMENT);
        p2.setListingType(ListingType.SALE);
        p2.setPrice(2200000.0);
        p2.setCity("New York");
        p2.setState("New York");
        p2.setAddress("75 Wall Street, Financial District");
        p2.setBedrooms(3);
        p2.setBathrooms(3);
        p2.setAreaSqFt(3100.0);
        p2.setStatus(PropertyStatus.AVAILABLE);
        p2.setImageUrl("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?auto=format&fit=crop&w=1200&q=80");
        p2.setAdditionalImages("https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80,https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80");
        p2.setOwner(seller);
        p2.setAgent(agent1);
        propertyRepository.save(p2);

        Property p3 = new Property();
        p3.setTitle("Chic Downtown Loft for Rent");
        p3.setDescription("Bright and airy open-concept timber loft in the heart of West Loop. Exposed brick walls, polished concrete flooring, stainless steel gas range, in-unit laundry, and heated indoor garage parking included.");
        p3.setPropertyType(PropertyType.APARTMENT);
        p3.setListingType(ListingType.RENT);
        p3.setPrice(3400.0);
        p3.setCity("Chicago");
        p3.setState("Illinois");
        p3.setAddress("820 W Fulton Market");
        p3.setBedrooms(2);
        p3.setBathrooms(2);
        p3.setAreaSqFt(1450.0);
        p3.setStatus(PropertyStatus.AVAILABLE);
        p3.setImageUrl("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=1200&q=80");
        p3.setAdditionalImages("https://images.unsplash.com/photo-1502005229762-ee152da7c5d6?auto=format&fit=crop&w=800&q=80");
        p3.setOwner(seller);
        p3.setAgent(agent1);
        propertyRepository.save(p3);

        Property p4 = new Property();
        p4.setTitle("Modern Hillside Family Residence with Garden");
        p4.setDescription("Beautifully remodeled mid-century modern home tucked in the Hollywood Hills. Features an open layout, custom quartz countertops, solar power system, manicured private lawn, and serene canyon views.");
        p4.setPropertyType(PropertyType.HOUSE);
        p4.setListingType(ListingType.SALE);
        p4.setPrice(980000.0);
        p4.setCity("Los Angeles");
        p4.setState("California");
        p4.setAddress("2104 Laurel Canyon Blvd");
        p4.setBedrooms(4);
        p4.setBathrooms(3);
        p4.setAreaSqFt(2900.0);
        p4.setStatus(PropertyStatus.AVAILABLE);
        p4.setImageUrl("https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=1200&q=80");
        p4.setAdditionalImages("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=800&q=80");
        p4.setOwner(seller);
        p4.setAgent(agent1);
        propertyRepository.save(p4);

        Property p5 = new Property();
        p5.setTitle("Executive Tech Corridor Office Suite for Lease");
        p5.setDescription("Prime commercial space ideal for tech startups, law practices, or consultancy firms. High-speed fiber internet, multiple glass conference rooms, break lounge, and easy access to public transit.");
        p5.setPropertyType(PropertyType.COMMERCIAL);
        p5.setListingType(ListingType.RENT);
        p5.setPrice(5200.0);
        p5.setCity("Seattle");
        p5.setState("Washington");
        p5.setAddress("500 Pine Street, Downtown");
        p5.setBedrooms(0);
        p5.setBathrooms(2);
        p5.setAreaSqFt(2200.0);
        p5.setStatus(PropertyStatus.AVAILABLE);
        p5.setImageUrl("https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1200&q=80");
        p5.setOwner(seller);
        p5.setAgent(agent1);
        propertyRepository.save(p5);

        Property p6 = new Property();
        p6.setTitle("Scenic Hill Country Contemporary Estate");
        p6.setDescription("Sprawling architectural masterpiece situated on 2 pristine private acres. Features soaring ceilings, wine cellar, outdoor kitchen, heated swimming pool, and private guest casita.");
        p6.setPropertyType(PropertyType.HOUSE);
        p6.setListingType(ListingType.SALE);
        p6.setPrice(875000.0);
        p6.setCity("Austin");
        p6.setState("Texas");
        p6.setAddress("3400 Barton Creek Blvd");
        p6.setBedrooms(4);
        p6.setBathrooms(4);
        p6.setAreaSqFt(3800.0);
        p6.setStatus(PropertyStatus.AVAILABLE);
        p6.setImageUrl("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=1200&q=80");
        p6.setOwner(seller);
        p6.setAgent(agent1);
        propertyRepository.save(p6);

        // 3. Create Sample Favorites for Buyer
        favoriteRepository.save(new Favorite(buyer, p1));
        favoriteRepository.save(new Favorite(buyer, p2));

        // 4. Create Sample Scheduled Visits
        VisitSchedule v1 = new VisitSchedule();
        v1.setProperty(p1);
        v1.setBuyer(buyer);
        v1.setVisitDate(LocalDate.now().plusDays(2));
        v1.setTimeSlot("11:00 AM - 12:00 PM");
        v1.setStatus(VisitStatus.APPROVED);
        v1.setNotes("Looking forward to touring the waterfront dock and master suite.");
        visitScheduleRepository.save(v1);

        VisitSchedule v2 = new VisitSchedule();
        v2.setProperty(p2);
        v2.setBuyer(buyer);
        v2.setVisitDate(LocalDate.now().plusDays(4));
        v2.setTimeSlot("02:00 PM - 03:00 PM");
        v2.setStatus(VisitStatus.PENDING);
        v2.setNotes("First time buyer exploring downtown Manhattan penthouses.");
        visitScheduleRepository.save(v2);

        // 5. Create Sample Inquiries
        Inquiry inq1 = new Inquiry();
        inq1.setProperty(p1);
        inq1.setSender(buyer);
        inq1.setReceiver(agent1);
        inq1.setSubject("HOA and Property Taxes Inquiry");
        inq1.setMessage("Hi Michael, could you provide the annual property tax statement and HOA fee breakdown for the Biscayne Bay Villa?");
        inq1.setPhone(buyer.getPhone());
        inq1.setStatus(InquiryStatus.NEW);
        inquiryRepository.save(inq1);

        // 6. Create Sample Notifications
        notificationRepository.save(new Notification(buyer, "Visit Confirmed", "Your visit for 'Luxury Waterfront Villa with Private Pool' has been approved for " + LocalDate.now().plusDays(2) + " (11:00 AM - 12:00 PM).", "VISIT"));
        notificationRepository.save(new Notification(agent1, "New Inquiry Received", "Buyer Emma Watson sent an inquiry on 'Luxury Waterfront Villa with Private Pool'.", "INQUIRY"));
        notificationRepository.save(new Notification(admin, "Agent Verification Request", "Sarah Jenkins has applied for broker verification with license CA-BR-554433.", "VERIFICATION"));
    }
}
