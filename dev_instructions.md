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

### 4. Run CLI Backend (Alternative)

The application also provides a command-line interface for cover letter generation.

**Prerequisites: Start MariaDB Database**

Option A - Using Docker (recommended):

```bash
# Start Docker Desktop first, then:
docker-compose up -d db

# Wait for database to be ready (about 10 seconds)
# The database will be automatically initialized with the schema
```

Option B - Using local MariaDB installation:

```bash
# Install MariaDB from https://mariadb.org/download/
# Create the database and tables:
mysql -u root -p < src/main/java/com/clbooster/app/backend/service/database/coverletter_generator_script.sql
```

**Run the CLI:**
Set API key and run CLI in one command (environment variable persists only for this session)
**Option A - PowerShell (recommended):**

```powershell

$env:GOOGLE_API_KEY="your_api_key_here"; mvn exec:java "-Dexec.mainClass=com.clbooster.app.views.CLgenerator_CLI"
```

**Option B - Command Prompt:**

```cmd
set GOOGLE_API_KEY=your_api_key_here && mvn exec:java "-Dexec.mainClass=com.clbooster.app.views.CLgenerator_CLI"
```

Note: On Windows, quotes around `-Dexec.mainClass` are required for proper argument parsing. The environment variable must be set in the same terminal session where Maven runs.

**Database Configuration:**

The CLI uses environment variables for database connection:

- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port (default: 3306)
- `DB_NAME` - Database name (default: CL_generator)
- `DB_USERNAME` - Database username (default: root)
- `DB_PASSWORD` - Database password (default: password)

**Test User Credentials:**

- Email: Test@User.com
- Username: TestUser
- Example password: `TestPass123!`
- 1st name: Test
- last name: User

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

# kill java process
taskkill /F /IM java.exe

# Then rebuild
mvn clean package -DskipTests -Pproduction

# run java
java -jar target/cl-booster-1.0-SNAPSHOT.jar
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

### test api key

curl -s "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=**key**" -H "Content-Type: application/json" -d '{"contents":[{"parts":[{"text":"Hello"}]}]}'
