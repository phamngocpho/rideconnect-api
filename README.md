# RideConnect API

RideConnect API is a Spring Boot-based backend service that provides REST endpoints for a ride-hailing application with Goong Maps integration. The system supports real-time tracking, payment processing, and trip management.

## Overview

- **Framework:** Spring Boot 3.4.4
- **Java Version:** JDK 21
- **Database:** PostgreSQL + PostGIS
- **Authentication:** JWT
- **Documentation:** Swagger UI
- **Real-time Communication:** WebSocket

## System Requirements

- Java JDK 21
- Maven 3.8.x or higher
- PostgreSQL 13+ with PostGIS extension
- Docker (optional)

## Tech Stack

### Core Dependencies
- Spring Boot 3.4.4
  - spring-boot-starter-web
  - spring-boot-starter-data-jpa
  - spring-boot-starter-security
  - spring-boot-starter-websocket
  - spring-boot-starter-validation

### Database & Spatial Data
- PostgreSQL 42.7.5
- PostGIS JDBC 2.5.0
- Hibernate Types 2.21.1

### Security
- JWT (JJWT 0.12.6)
- Spring Security

### Utils & Others
- Lombok
- Jackson
- Apache Commons Lang3
- Thymeleaf
- WebFlux

## Project Structure

```
com.rideconnect
├── config/                 # Application configurations
│   ├── SecurityConfig     # Security and JWT config
│   ├── WebSocketConfig    # WebSocket configuration
│   └── GoongMapConfig     # Goong Maps API config
├── controller/            # REST Controllers
│   ├── AuthController     # Authentication endpoints
│   ├── LocationController # Location management
│   ├── MessageController  # Messaging
│   ├── NotificationController # Notifications
│   ├── PaymentController  # Payment processing
│   ├── RatingController   # Rating system
│   └── TripController     # Trip management
├── model/                # Entities and DTOs
├── repository/          # Data access layer
├── service/            # Business logic
├── security/          # JWT and authentication
└── util/             # Utility classes
```

## Installation & Setup

### 1. PostgreSQL and PostGIS Setup

#### Using psql CLI:
```bash
# Connect to PostgreSQL using psql
psql -U postgres

# Then run these commands
CREATE DATABASE rideconnect;
\c rideconnect
CREATE EXTENSION postgis;
```

#### Using SQL queries:
```sql
-- Run these SQL commands in your database client
CREATE DATABASE rideconnect;
-- Connect to rideconnect database first, then:
CREATE EXTENSION postgis;
```

#### Using pgAdmin:
1. Create new database named 'rideconnect'
2. Connect to 'rideconnect' database
3. Open Query Tool
4. Execute: `CREATE EXTENSION postgis;`

### 2. Environment Configuration

Create `application-dev.properties` file in the `src/main/resources` directory:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/rideconnect
spring.datasource.username=your_username
spring.datasource.password=your_password

# Goong Maps
goong.api.key=your_api_key
goong.maptiles.key=your_maptiles_key

# JWT
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Server
server.port=8080
```

### 3. Application Deployment

#### Using Maven

```bash
git clone https://github.com/phamngocpho/rideconnect-api.git
cd rideconnect-api
./mvnw clean

# Run with default profile
./mvnw spring-boot:run

# Run with dev profile (Linux/macOS)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run with dev profile (Windows - CMD)
mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"

# Run with dev profile (Windows - PowerShell)
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"
```

#### Using Docker

```bash
docker build -t rideconnect-api .
docker run -p 8080:8080 --env-file .env rideconnect-api
```
## Application Profiles

The application supports different profiles for various environments:

- **dev**: Development environment with debug logging and H2 console enabled
- **prod**: Production environment with optimized settings
- **test**: Testing environment for automated tests

To run with a specific profile, use one of the commands below:

```bash
# Using Maven
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Using JAR file
java -jar target/RideConnectApplication-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Create new account |
| POST | `/api/auth/login` | User authentication |
| POST | `/api/auth/refresh` | Refresh access token |

### Location
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/locations/update` | Update location |
| GET | `/api/locations/nearby-drivers` | Find nearby drivers |

### Trips
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/trips` | Create new trip |
| GET | `/api/trips/{tripId}` | Get trip details |
| PUT | `/api/trips/{tripId}/status` | Update trip status |
| GET | `/api/trips/history` | Get trip history |

### Payments
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments` | Create new payment |
| GET | `/api/payments/{paymentId}` | Get payment details |
| GET | `/api/payments/methods` | List payment methods |

### Notifications
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/notifications` | Get notifications |
| PUT | `/api/notifications/{id}/read` | Mark as read |

## Security

- JWT Authentication
- CORS configured for specified origins
- Protected endpoints (except /auth/**, /ws/**, /swagger-ui/**)
- BCrypt password encoding
- Stateless session management

## WebSocket

WebSocket endpoints for real-time features:
```
ws://your-domain/ws
```

## Documentation

- Project Info: `http://localhost:8080/api/project`
  - Provides a responsive web interface with:
    - Project overview and features
    - Technical stack information
    - Developer information
    - Supports both English and Vietnamese languages

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Contact

For questions or issues, please create an issue in the repository or contact us directly.
