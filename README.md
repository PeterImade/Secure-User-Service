![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)

# User Service

Spring Boot 4 authentication service with:
- JWT access tokens
- Refresh token rotation with theft detection
- Role-based access control (ADMIN / USER)
- Full CRUD operations
- Global exception handling

## 🛠️ Tech Stack

- **Java 17** – Core language
- **Spring Boot 4.1.0** – Framework
- **Spring Security** – Authentication & Authorization
- **Spring Data JPA** – ORM and database access
- **PostgreSQL** – Production database (H2 for local testing)
- **JWT (JJWT)** – Access token generation and validation
- **Lombok** – Boilerplate reduction
- **Maven** – Build tool and dependency management
- **Docker** – Containerization

## Setup

1. Clone the repo
2. Copy `application.properties.template` to `application.properties`
3. Update database credentials
4. Run with `mvn spring-boot:run`

## API Endpoints

- `POST /api/auth/signup` - Register a new user
- `POST /api/auth/login` - Login and get tokens
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - Logout and revoke refresh token
- `GET /api/users` - List all users (ADMIN only)
- `GET /api/users/{id}` - Get user by ID (ADMIN only)
- `PUT /api/users/{id}` - Update user (ADMIN only)
- `DELETE /api/users/{id}` - Delete user (ADMIN only)
