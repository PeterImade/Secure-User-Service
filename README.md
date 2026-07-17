# User Service

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-brightgreen)

Spring Boot 4 authentication service with:

- JWT access tokens
- Refresh token rotation with theft detection
- Role-based access control (ADMIN / USER)
- Full CRUD operations
- Global exception handling

---

## 🚀 Live Demo

**Base URL:** [https://secure-user-service.onrender.com](https://secure-user-service.onrender.com)

**Swagger UI:** [https://secure-user-service.onrender.com/swagger-ui/index.html](https://secure-user-service.onrender.com/swagger-ui/index.html)

> ⚠️ **Note:** This demo runs on Render's free tier. The PostgreSQL database is temporary and may expire after 30 days. If the API is unresponsive or returns errors, the database may have been reset. The code is fully functional — run it locally with Docker to test all features.

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.1.0 |
| Security | Spring Security |
| Data Access | Spring Data JPA |
| Database | PostgreSQL |
| Auth Tokens | JWT (JJWT) |
| Boilerplate Reduction | Lombok |
| Build Tool | Maven |
| Containerization | Docker |
| CI/CD | GitHub Actions |

---

## 📄 API Documentation (Swagger)

Once the app is running locally, access Swagger UI at:

```
http://localhost:8080/swagger-ui/index.html
```

You can test all endpoints directly from the browser. Click **"Authorize"** to add your JWT token for protected endpoints.

---

## ⚙️ Setup

1. Clone the repo
2. Copy `application.properties.template` to `application.properties`
3. Update database credentials
4. Run with:
   ```bash
   mvn spring-boot:run
   ```

---

## 📚 API Endpoints

### Auth

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/signup` | Register a new user |
| `POST` | `/api/auth/login` | Login and get tokens |
| `POST` | `/api/auth/refresh` | Refresh access token |
| `POST` | `/api/auth/logout` | Logout and revoke refresh token |

### Users *(ADMIN only)*

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/users` | List all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `PUT` | `/api/users/{id}` | Update user |
| `DELETE` | `/api/users/{id}` | Delete user |

---

## 🐳 Running with Docker

```bash
# Build the image
docker build -t secure-user-service .

# Start the app + PostgreSQL
docker-compose up -d

# Stop everything
docker-compose down
```

---

## 🔄 Deployment Workflow

```
git push origin main
        ↓
GitHub Actions triggers
        ↓
1. Checks out code
2. Sets up JDK 17
3. Builds JAR with Maven
4. Logs in to Docker Hub
5. Builds and pushes Docker image
6. Calls Render Deploy Hook
        ↓
Render pulls latest image
        ↓
App is LIVE 🎉
```

<img width="1354" height="609" alt="Deployment workflow diagram" src="https://github.com/user-attachments/assets/5e05a712-9d66-45f8-afe6-fb94748c2df9" />
