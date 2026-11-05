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
| **Containerization** | **Docker & Docker Compose** | Multi-stage image build and isolated multi-container setup |
| **API Testing** | **Postman Collection (v2.1)** | Pre-configured collection for all REST endpoints |
| **Frontend** | **HTML5, CSS3, JavaScript (ES6), Bootstrap 5.3** | Responsive UI, interactive modals, toasts, dynamic headers |

---

## 👥 User Roles & Access Control

1. **Buyer (`ROLE_BUYER`)**:
   - Search & filter listings by city, price range, property type, and sale/rent mode.
   - Interactive mortgage EMI loan estimator on property pages.
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
├── postman/
│   └── RealEstateHub_API_Collection.json # Postman collection for all REST APIs
├── docker-compose.yml                # Docker Compose multi-container configuration
├── .dockerignore                     # Docker build exclusion rules
├── backend/
│   ├── Dockerfile                    # Multi-stage Dockerfile (JDK build + JRE runtime)
│   ├── mvnw & mvnw.cmd               # Maven wrappers for Windows and Unix
│   ├── pom.xml                       # Maven dependency management
│   └── src/
│       ├── main/
│       │   ├── java/com/realestate/backend/
│       │   │   ├── BackendApplication.java       # Spring Boot main entry point
│       │   │   ├── HomeController.java           # Health check endpoint (/api/health)
│       │   │   ├── config/WebConfig.java         # Static resource routing and view mappings
│       │   │   ├── controller/                   # REST API controllers
│       │   │   ├── dto/                          # Clean data transfer objects & ApiResponse
│       │   │   ├── exception/                    # GlobalExceptionHandler & Custom exceptions
│       │   │   ├── model/                        # JPA Entities: User, Property, Favorite, Visit, Inquiry
│       │   │   ├── repository/                   # Spring Data JPA Repositories
│       │   │   ├── security/                     # Spring Security & JWT Filter
│       │   │   └── service/                      # Business services & DataInitializer
│       │   └── resources/
│       │       ├── application.properties        # MySQL connection and JWT parameters
│       │       ├── schema.sql                    # Standalone MySQL DDL schema
│       │       ├── data.sql                      # Initial seed documentation
│       │       └── static/                       # Production web UI bundle served by Spring Boot
│       └── test/java/com/realestate/backend/     # JUnit 5 & Mockito service unit tests
├── frontend/                                     # Standalone frontend files (HTML/CSS/JS)
│   ├── css/styles.css                            # Custom design system & badges
│   ├── js/                                       # Reusable API client, mortgage calculator, and UI logic
│   ├── index.html                                # Homepage & Hero search
│   ├── properties.html                           # Full catalog with dynamic filters
│   ├── property-details.html                     # Specifications, gallery, agent badge, EMI calculator
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

---

## 🚀 Running the Project

### Option A: Running in Visual Studio Code (Local)
1. Open **VS Code** ➔ **Open Folder** ➔ Select the project directory.
2. Ensure **Extension Pack for Java** is installed.
3. Open `BackendApplication.java` and click **Run** (or press **F5**).
4. Access the application in your browser: **`http://localhost:8080`**

### Option B: Running with Docker Compose
To run both MySQL and the Spring Boot application in isolated containers:
```bash
docker compose up --build
```
Access the application at **`http://localhost:8080`**.
