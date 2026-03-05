# UrbanBites - Restaurant Reservation System

A complete full-stack application for managing restaurant reservations, menus, and dining experiences. Built with Spring Boot, Angular, and PostgreSQL.

##  Project Overview

UrbanBites is a comprehensive restaurant reservation platform that allows users to:
- Browse and explore restaurants
- Make and manage reservations
- View restaurant menus
- Leave reviews and feedback
- Manage dining experiences

The system provides different roles and features for:
- **Regular Users**: Browse restaurants, make reservations, leave feedback
- **Restaurant Admins**: Manage menus, tables, reservations, and view analytics
- **System Admins**: Overall platform management and user administration

##  Project Structure

```
proiect-paw-gio/
├── backend/                    # Spring Boot REST API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/gio/backend/
│   │   │   │   ├── controller/         # REST endpoints
│   │   │   │   ├── service/            # Business logic
│   │   │   │   ├── entity/             # JPA entities
│   │   │   │   ├── repository/         # Data access
│   │   │   │   ├── dto/                # Data transfer objects
│   │   │   │   ├── config/             # Application configuration
│   │   │   │   ├── security/           # Security & JWT
│   │   │   │   ├── aspect/             # AOP aspects
│   │   │   │   └── enums/              # Enumerations
│   │   │   └── resources/
│   │   │       └── db/migration/       # Flyway migrations
│   │   └── test/
│   ├── pom.xml                 # Maven dependencies
│   └── README.md               # Backend documentation
│
├── UrbanBitesFrontend/         # Angular web application
│   ├── src/
│   │   ├── app/
│   │   │   ├── admin/          # Admin dashboard components
│   │   │   ├── auth/           # Authentication (login, register, 2FA)
│   │   │   ├── core/           # Core services, guards, interceptors
│   │   │   ├── restaurants/    # Restaurant browsing
│   │   │   ├── rezervari/      # Reservations module
│   │   │   ├── feedback/       # Reviews and ratings
│   │   │   ├── meniu/          # Menu management
│   │   │   ├── profile/        # User profile
│   │   │   ├── notifications/  # Notification center
│   │   │   ├── shared/         # Shared components and models
│   │   │   └── services/       # Application services
│   │   ├── assets/             # Static assets
│   │   └── environments/       # Environment configurations
│   ├── package.json            # npm dependencies
│   ├── angular.json            # Angular CLI config
│   ├── IMPLEMENTATION.md       # Frontend implementation details
│   └── README.md               # Frontend documentation
│
└── db/                         # Database migrations
    ├── migration/
    │   ├── V1__create_base_tables.sql
    │   ├── V2__create_foreign_keys.sql
    │   ├── V3__create_triggers_and_functions.sql
    │   ├── V4__create_views.sql
    │   ├── V5__insert_test_data.sql
    │   ├── V6__allow_null_guest_reservations.sql
    │   ├── V7__create_activity_logs.sql
    │   └── V8__allow_null_user_id_payments.sql
    └── README.md               # Database documentation
```

##  Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.7
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Migrations**: Flyway
- **Security**: JWT (JSON Web Tokens)
- **API Documentation**: Spring Doc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Additional**: Lombok, Spring Validation

### Frontend
- **Framework**: Angular 21.0.2
- **Language**: TypeScript 5.9
- **Styling**: Bootstrap 5.3.8, CSS
- **Package Manager**: npm 11.6.2
- **Testing**: Vitest
- **Server-Side Rendering**: Angular SSR

### Database
- **DBMS**: PostgreSQL
- **Versioning**: Flyway
- **Key Features**:
  - UUID support with `uuid-ossp` extension
  - JSONB for flexible data storage
  - Triggers for automatic timestamp updates
  - Views for complex queries
  - Full-text search capabilities

##  Prerequisites

### System Requirements
- **Java**: JDK 17 or higher
- **Node.js**: 18.x or higher
- **npm**: 11.6.2 or higher
- **PostgreSQL**: 12 or higher
- **Maven**: 3.6 or higher (or use `mvnw` script)

### Accounts & Services
- PostgreSQL database server running and accessible
- A database named `urbanbites` created

##  Getting Started

### 1. Database Setup

#### Prerequisites
Ensure PostgreSQL is installed and running.

#### Create Database
```bash
psql -U postgres
```

In PostgreSQL console:
```sql
CREATE DATABASE urbanbites;
CREATE USER urbanbites_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE urbanbites TO urbanbites_user;
\q
```

#### Run Migrations
Flyway migrations will run automatically on application startup. Current migrations:
- **V1**: Base tables (users, restaurants, tables, reservations, menus, etc.)
- **V2**: Foreign key constraints
- **V3**: Triggers and automatic functions
- **V4**: Database views for analytics
- **V5**: Test data insertion
- **V6**: Allow nullable guest reservations
- **V7**: Activity logging
- **V8**: Allow nullable user payments

### 2. Backend Setup

```bash
cd backend

# Configure database connection
# Edit src/main/resources/application.properties:
# - spring.datasource.url
# - spring.datasource.username
# - spring.datasource.password
# OR set environment variables (see application.properties)

# Install dependencies and run
./mvnw clean install
./mvnw spring-boot:run
```

The backend will be available at `http://localhost:8080`

**Environment Variables** (used in `application.properties`):
```
DB_HOST=localhost              # PostgreSQL host
DB_PORT=5432                   # PostgreSQL port
DB_NAME=urbanbites            # Database name
DB_USERNAME=urbanbites_user   # Database user
DB_PASSWORD=secure_password   # Database password
DB_POOL_SIZE=10               # Connection pool size
SERVER_PORT=8080              # Application port
JWT_SECRET=<your-secret-key>  # JWT secret for authentication
JWT_EXPIRATION=86400000       # Token expiration (24 hours)
CORS_ALLOWED_ORIGINS=http://localhost:4200  # Allowed CORS origins
SPRING_PROFILES_ACTIVE=dev    # Active Spring profile
```

### 3. Frontend Setup

```bash
cd UrbanBitesFrontend

# Install dependencies
npm install

# Start development server
npm start
# or
ng serve
```

The application will be available at `http://localhost:4200`

### 4. Verify Setup

1. **Backend**: Open `http://localhost:8080/api` - should show API documentation
2. **Frontend**: Open `http://localhost:4200` - should load the Angular application
3. **Database**: Connect with `psql -U urbanbites_user -d urbanbites` to verify data

##  API Documentation

### Backend API Endpoints

The backend provides a comprehensive REST API. API documentation is available at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Main API Categories

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - User logout

#### Users & Profile
- `GET /api/users/{id}` - Get user profile
- `PUT /api/users/{id}` - Update user profile
- `GET /api/users/{id}/reservations` - Get user's reservations

#### Restaurants
- `GET /api/restaurants` - List all active restaurants
- `GET /api/restaurants/{id}` - Get restaurant details
- `GET /api/restaurants/{id}/tables` - Get available tables
- `GET /api/restaurants/{id}/menus` - Get restaurant menus

#### Reservations
- `POST /api/reservations` - Create new reservation
- `GET /api/reservations` - List reservations
- `GET /api/reservations/{id}` - Get reservation details
- `PUT /api/reservations/{id}` - Update reservation
- `DELETE /api/reservations/{id}` - Cancel reservation

#### Feedback & Reviews
- `POST /api/feedback` - Submit review
- `GET /api/feedback/restaurant/{id}` - Get restaurant reviews
- `GET /api/feedback/{id}` - Get feedback details

#### Menus
- `GET /api/menus` - List all menus
- `GET /api/menus/{id}` - Get menu details
- `GET /api/menu-items` - List menu items
- `GET /api/menu-items/{id}` - Get menu item details

##  Security Features

- **JWT Authentication**: Secure token-based authentication
- **CORS Configuration**: Controlled cross-origin requests
- **Password Security**: Bcrypt hashing
- **Role-Based Access Control**: Different permissions for users, admins, and system admins
- **Two-Factor Authentication**: 2FA setup and verification support

##  Key Features

### User Features
-  Browse restaurants with filtering and search
-  Make reservations with date/time/party size selection
-  View reservation history and status
-  Leave ratings and detailed reviews
-  View restaurant menus and items
-  Manage user profile
-  Two-factor authentication

### Restaurant Admin Features
-  Manage restaurant information and opening hours
-  Manage tables and seating arrangements
-  View and manage reservations
-  Create and edit menus and menu items
-  View analytics and statistics
-  Manage payments and transactions

### System Admin Features
-  User account management
-  Restaurant and menu oversight
-  Payment management
-  Contact message handling
-  Activity logging and audit trail
-  Analytics and reporting

##  Workflow Examples

### Making a Reservation
1. User browses restaurants
2. Selects restaurant and clicks "Reserve"
3. Chooses date, time, and party size
4. System checks table availability
5. Reservation is created with confirmation code
6. Confirmation sent to user email

### Leaving Feedback
1. User navigates to feedback section
2. Selects restaurant
3. Fills out detailed review with ratings
4. Submits feedback
5. Restaurant rating automatically updated

##  Build & Deployment

### Frontend Build
```bash
cd UrbanBitesFrontend
npm run build
# Output: dist/UrbanBitesFrontend/
```

### Backend Build
```bash
cd backend
./mvnw clean package
# Output: target/backend-0.0.1-SNAPSHOT.jar
```

### Production Environment Variables
```
SPRING_PROFILES_ACTIVE=prod
DB_HOST=<production-db-host>
DB_NAME=urbanbites_prod
DB_USERNAME=<prod-username>
DB_PASSWORD=<prod-password>
JWT_SECRET=<strong-secret-key>
CORS_ALLOWED_ORIGINS=https://yourdomain.com
```

##  Testing

### Frontend Tests
```bash
cd UrbanBitesFrontend
npm test
```

### Backend Tests
```bash
cd backend
./mvnw test
```


##  Documentation

- [Backend README](backend/README.md) - Backend-specific setup and API details
- [Frontend README](UrbanBitesFrontend/README.md) - Frontend setup and structure
- [Frontend Implementation](UrbanBitesFrontend/IMPLEMENTATION.md) - Feature documentation
- [Database README](db/README.md) - Database schema and migrations

##  Quick Links

- **Swagger API Docs**: http://localhost:8080/swagger-ui.html
- **Frontend Development**: http://localhost:4200
- **Database**: localhost:5432 (urbanbites)


##  License

This project is part of the PAW (Project Architecture Web) course at the Faculty of Engineering.


**Last Updated**: March 2026
**Version**: 1.0

