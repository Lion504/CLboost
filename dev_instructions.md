# CL Booster - Development Instructions

## Prerequisites

- Java 25.0.1
- Maven 3.6+
- Google Gemini API key
- Docker & Docker Compose

## Local Development Setup

### 1. Clone and Configure

```bash
git clone <repository-url>
cd cl-booster
```

### 2. Environment Variables

Copy the example environment file and configure:

```bash
cp .env.example .env
```

Edit `.env` with your values:

```bash
GEMINI_API_KEY=your_api_key_here
PORT=8080
```

### 3. Build and Run

```bash
# Build the application
mvn clean install

# Run in development mode
mvn spring-boot:run
```

Application will be available at http://localhost:8080 by default

## Docker Development

### Using Docker Compose

```bash
# Start the application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down
```

### Testing Environment

```bash
# Run tests
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

## Project Structure

```
cl-booster/
├── src/main/java/com/clbooster/app/
│   ├── Application.java                    # Spring Boot entry point
│   ├── backend/
│   │   ├── config/                         # SecurityConfig, AiConfig
│   │   ├── entity/                         # User, Resume, CoverLetter
│   │   ├── repository/                     # Interfaces for DB access
│   │   ├── service/                        # ResumeService, AiService
│   │   └── util/
│   └── views/                              # Java UI views
├── src/main/frontend/                      # Vaadin client-side resources
│   ├── index.ts                            # Entry point
│   ├── themes/                             # Custom themes
│   └── generated/                          # Generated frontend files
├── src/main/resources/
│   ├── application.properties              # Configuration
│   └── META-INF/resources/                 # Static assets
├── docker-compose.yml                      # Development environment
└── Dockerfile                              # Container build
```

## Development Guidelines

### Code Style

- Follow Eclipse formatter configuration (`eclipse-formatter.xml`)
- Use Spotless for code formatting: `mvn spotless:apply`
- Check code formatting: `mvn spotless:check`
- Check code style compliance: `mvn checkstyle:check`
- Maximum line length: 120 characters

### Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify -Pit

# Run with coverage
mvn test jacoco:report
```

## Deployment

### Production Build

```bash
# Build production JAR
mvn clean package -Pproduction

# Build Docker image
docker build -t cl-booster:latest .
```

### Logs

- Application logs: `logs/spring.log`
- Vaadin logs: Console output during development
- Docker logs: `docker-compose logs`

## Contributing

1. Create feature branch from `develop`
2. Make changes following guidelines
3. Add tests for new functionality
4. Ensure CI passes
5. Submit pull request
