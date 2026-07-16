# User Service

Spring Boot 4 authentication service with:
- JWT access tokens
- Refresh token rotation with theft detection
- Role-based access control (ADMIN / USER)
- Full CRUD operations
- Global exception handling

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
