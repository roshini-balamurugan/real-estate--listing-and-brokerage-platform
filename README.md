# Real Estate Listing and Brokerage Platform
**College Capstone Engineering Project**

A modern full-stack Real Estate Listing and Brokerage Platform built with **Java Spring Boot**, **MySQL**, **Spring Data JPA**, **Spring Security (JWT + BCrypt)**, and **Bootstrap 5**.

---

## 🏗️ Technology Stack

| Layer | Technology | Details |
|---|---|---|
| **Backend** | **Java 17 / 21 + Spring Boot** | Pure Java backend with RESTful web services |
| **Database** | **MySQL 8.x / 26.x** | Relational schema with foreign keys and automated migrations |
| **ORM / Data Access**| **Spring Data JPA & Hibernate** | Object-relational mapping, custom JPQL queries |
| **Security** | **Spring Security + JJWT 0.12.6** | BCrypt password hashing, stateless JWT authentication, role guards |
| **Build Tool** | **Maven Wrapper (`mvnw` / `mvnw.cmd`)** | Zero-dependency build tool included |
| **Frontend** | **HTML5, CSS3, JavaScript (ES6), Bootstrap 5.3** | Responsive UI, interactive modals, toasts, dynamic headers |

---

## 👥 User Roles & Access Control

1. **Buyer (`ROLE_BUYER`)**:
   - Search & filter listings by city, price range, property type, and sale/rent mode.
   - Save properties to favorites / wishlist.
   - Schedule on-site property tours with preferred date and time slot.
   - Send direct inquiries to property owners and licensed agents.
   - Track tour statuses, sent inquiries, and notifications in real time.

2. **Seller (`ROLE_SELLER`)**:
   - Publish residential and commercial property listings with pricing, specifications, and images.
   - Manage published properties (edit details, change status to AVAILABLE / PENDING / SOLD / RENTED, or delete).
   - Review incoming property tour requests (Approve, Reject, or Mark Complete).
   - View and respond to buyer inquiries.

3. **Broker / Agent (`ROLE_AGENT`)**:
   - Register with brokerage details (Agency Name & Broker License Number).
   - Submit credentials for Administrator verification.
   - Once verified, receive a **Verified Broker Badge** on all public listings.
   - Publish client properties and host scheduled buyer visits.

4. **Administrator (`ROLE_ADMIN`)**:
   - Super admin console with live system KPIs (total users, active properties, scheduled tours, pending verifications).
   - **Agent Verification Center**: Review pending agent applications, inspect license numbers, and Approve or Reject with instant notification dispatch.
   - User Account Management: Inspect registered buyers, sellers, agents, and delete accounts safely.
   - Listings Oversight: Inspect and remove any published property platform-wide.

---

## 📁 Project Architecture & Folder Structure

```
c:\real estate listing and brokerage platform/
├── .vscode/
│   ├── launch.json                   # One-click F5 run configuration for VS Code
│   ├── tasks.json                    # VS Code build & launch tasks
│   └── settings.json                 # Java project configuration
├── backend/
│   ├── mvnw & mvnw.cmd               # Maven wrappers for Windows and Unix
│   ├── pom.xml                       # Maven dependency management
│   └── src/
│       ├── main/
│       │   ├── java/com/realestate/backend/
│       │   │   ├── BackendApplication.java       # Spring Boot main entry point
│       │   │   ├── HomeController.java           # Health check endpoint (/api/health)
│       │   │   ├── config/
│       │   │   │   └── WebConfig.java            # Static resource routing and view mappings
│       │   │   ├── controller/
│       │   │   │   ├── AuthController.java       # Register, Login, Me endpoints
│       │   │   │   ├── PropertyController.java   # CRUD, search, filter, /my listings
│       │   │   │   ├── FavoriteController.java   # Add, remove, check, list favorites
│       │   │   │   ├── VisitController.java      # Schedule, host approvals, buyer tours
│       │   │   │   ├── InquiryController.java    # Send, receive, update reply status
│       │   │   │   ├── NotificationController.java# List, unread count, mark read
│       │   │   │   ├── AdminController.java      # Stats, users, agent verification
│       │   │   │   └── AgentController.java      # Verified agents directory
│       │   │   ├── dto/                          # Clean data transfer objects
│       │   │   ├── model/                        # JPA Entities: User, Property, Favorite, VisitSchedule, Inquiry, Notification
│       │   │   ├── repository/                   # Spring Data JPA Repositories
│       │   │   ├── security/                     # SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService
│       │   │   └── service/                      # Business logic layer & DataInitializer
│       │   └── resources/
│       │       ├── application.properties        # MySQL connection and JWT parameters
│       │       └── static/                       # Production web UI bundle served by Spring Boot
├── frontend/                                     # Standalone frontend files (HTML/CSS/JS)
│   ├── css/styles.css                            # Custom design system & badges
│   ├── js/
│   │   ├── api.js                                # Reusable API client, tokens & toasts
│   │   ├── auth.js                               # Global navbar, notifications & session
│   │   ├── main.js                               # Homepage logic & featured listings
│   │   ├── properties.js                         # Search, filter, and property cards
│   │   └── property-details.js                   # Gallery, visit scheduling & inquiries
│   ├── index.html                                # Homepage & Hero search
│   ├── properties.html                           # Full catalog with dynamic filters
│   ├── property-details.html                     # Specifications, gallery, agent badge
│   ├── login.html                                # Sign in with 1-click demo autofill
│   ├── register.html                             # Buyer, Seller, Agent registration
│   ├── dashboard-buyer.html                      # Buyer portal (Favorites, Tours, Inquiries)
│   ├── dashboard-seller.html                     # Seller & Agent portal (Listings & Bookings)
│   └── dashboard-admin.html                      # Admin console (Metrics, Agent Verification)
└── README.md
```

---

## 🔑 Pre-Seeded Demonstration Accounts

For viva presentation and quick evaluation, the application automatically seeds the database on initial startup:

| Role | Email Address | Password | Description / State |
|---|---|---|---|
| **Admin** | `admin@realestate.com` | `admin123` | Platform Super Administrator |
| **Agent** | `agent@realestate.com` | `agent123` | **Verified** Licensed Broker (*Prime Manhattan Realty*) |
| **Agent (Pending)** | `sarah.agent@realestate.com` | `agent123` | **Pending Verification** (*Pacific Coast Estates*) |
| **Seller** | `seller@realestate.com` | `seller123` | Property owner with pre-listed properties |
| **Buyer** | `buyer@realestate.com` | `buyer123` | Buyer with saved favorites and active tours |

> *Tip: On `login.html`, click any of the 5 colored quick-fill demo buttons to immediately autofill credentials without typing!*

---

## 🚀 Running the Project in Visual Studio Code

### Step 1: Open the Project
1. Open **Visual Studio Code**.
2. Go to **File** ➔ **Open Folder...** and select:
   `c:\real estate  listing and brokerage platform`

### Step 2: Ensure Recommended Extensions Are Installed
Install the following extensions from the VS Code Marketplace:
- **Extension Pack for Java** (by Microsoft)
- **Spring Boot Extension Pack** (by VMware)

### Step 3: Verify MySQL Configuration
Ensure MySQL server is running. In `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/realestate?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=roshini247
```
*(If your local MySQL root password differs, update `spring.datasource.password` accordingly).*

### Step 4: Run the Application
You can run the project using any of these simple methods:

#### Method A: Using the VS Code Run Menu (Recommended)
1. Navigate to:
   `backend/src/main/java/com/realestate/backend/BackendApplication.java`
2. Click the **Run** button above the `main` method (or press **F5**).

#### Method B: Using VS Code Integrated Terminal
1. Open a terminal in VS Code (**Terminal** ➔ **New Terminal**).
2. Run:
   ```powershell
   cd backend
   .\mvnw.cmd spring-boot:run
   ```

### Step 5: Access the Web Application
Open your browser and navigate to:
👉 **`http://localhost:8080`**

Spring Boot serves both the Java REST APIs and the HTML/JS frontend unified from a single port without CORS or cross-origin hurdles!

---

## 🎯 Capstone Demonstration Walkthrough (Viva Guide)

1. **Homepage & Catalog**:
   - Open `http://localhost:8080`.
   - Use the Hero search bar to search for `"Miami"` or `"New York"`.
   - Filter properties for **Sale** or **Rent**, select property type (Apartment, Villa, House), and view instant results.

2. **Property Details & Inquiries**:
   - Click on any property (e.g. *Luxury Waterfront Villa*).
   - View high-resolution image gallery, specs (Bedrooms, Bathrooms, Sqft), and the listing agent's **Verified Broker Badge**.
   - Click **Schedule a Visit** to choose a date/time slot.
   - Submit an inquiry using the quick message box.

3. **Buyer Dashboard**:
   - Log in as `buyer@realestate.com` (`buyer123`).
   - View your saved favorites, scheduled tour statuses (Pending / Approved), and inquiry history.
   - Click the heart icon to add or remove favorites on the fly.

4. **Seller & Agent Portal**:
   - Log in as `agent@realestate.com` (`agent123`).
   - View your published listings, incoming tour requests, and received inquiries.
   - Click **Approve** or **Reject** on tour requests.
   - Add a new property listing with price, city, beds, baths, and image URL.

5. **Admin Console & Agent Verification**:
   - Log in as `admin@realestate.com` (`admin123`).
   - Open the **Agent Verification Center** tab.
   - Observe the pending license application for *Sarah Jenkins* (`sarah.agent@realestate.com`).
   - Click **Approve**.
   - Log in as `sarah.agent@realestate.com` (`agent123`) to show that her dashboard banner immediately changes to **Verified Broker License** and property creation is unlocked!
