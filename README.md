![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-green)
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

## 📄 API Documentation (Swagger)

Once the app is running, access Swagger UI at:
http://localhost:8080/swagger-ui/index.html

You can test all endpoints directly from the browser. Click **"Authorize"** to add your JWT token for protected endpoints.

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

## Running with Docker

```bash
# Build the image
docker build -t secure-user-service .

# Start the app + PostgreSQL
docker-compose up -d

# Stop everything
docker-compose down
