# CL Booster - Development Instructions

## Prerequisites

- Java 21+
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
GEMINI_API_KEY=your_gemini_api_key_here
GOOGLE_PROJECT_ID=your_google_project_id
GOOGLE_LOCATION=us-central1
PORT=8080
```

The app uses `spring-dotenv` to auto-load `.env` at startup. No need to set OS environment variables manually in development — just fill in `.env`.

### 3. Build and Run

```powershell
# Build the application
mvn clean install

# Recommended — .env handles API keys automatically
mvn spring-boot:run -DskipTests

# Manual override if needed (PowerShell — single line)
$env:GEMINI_API_KEY="key"; $env:GOOGLE_PROJECT_ID="your-project-id"; mvn spring-boot:run -DskipTests
```

Application will be available at http://localhost:8080 by default.

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

**Option A - PowerShell (recommended):**

```powershell
$env:GEMINI_API_KEY="your_api_key_here"; mvn exec:java "-Dexec.mainClass=com.clbooster.app.views.CLgenerator_CLI"
```

**Option B - Command Prompt:**

```cmd
set GEMINI_API_KEY=your_api_key_here && mvn exec:java "-Dexec.mainClass=com.clbooster.app.views.CLgenerator_CLI"
```

> Note: On Windows, quotes around `-Dexec.mainClass` are required. The environment variable must be set in the same terminal session where Maven runs.

**Database Configuration:**

The CLI uses environment variables for database connection:

- `DB_HOST` — Database host (default: `localhost`)
- `DB_PORT` — Database port (default: `3306`)
- `DB_NAME` — Database name (default: `CL_generator`)
- `DB_USERNAME` — Database username (default: `root`)
- `DB_PASSWORD` — Database password (default: `password`)

**Test credentials** are defined in:
`src/main/java/com/clbooster/app/backend/service/database/coverletter_generator_script.sql`

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
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

## Project Structure

```
cl-booster/
├── src/main/java/com/clbooster/
│   ├── aiservice/                          # AI integration
│   │   ├── AIService.java                  # Gemini API calls
│   │   ├── Exporter.java                   # DOCX/PDF export
│   │   └── Parser.java                     # Resume file parser
│   └── app/
│       ├── Application.java                # Spring Boot entry point
│       ├── backend/
│       │   ├── config/                     # SecurityConfig, AiConfig
│       │   └── service/
│       │       ├── authentication/         # AuthenticationService, SignUpView wiring
│       │       ├── database/               # UserDAO, SQL scripts
│       │       ├── document/               # DocumentService (upload/download/export)
│       │       └── profile/                # ProfileService, ProfileDAO, UserService, User
│       └── views/                          # Vaadin UI views
│           ├── MainLayout.java             # App shell, sidebar nav
│           ├── DashboardView.java
│           ├── GeneratorWizardView.java     # 5-step cover letter wizard
│           ├── HistoryView.java
│           ├── ResumeManagerView.java
│           ├── ProfileView.java
│           ├── SettingsView.java
│           ├── SignUpView.java
│           └── LoginView.java
├── src/main/resources/
│   ├── application.properties              # Configuration (keys via .env)
│   ├── i18n/                               # Translation files (en, fi, sv, de, fr)
│   └── META-INF/resources/                 # Static assets
├── uploads/                                # Runtime file storage (gitignored)
│   ├── resumes/                            # Uploaded resume files
│   └── coverletters/                       # Generated cover letter files
├── .env                                    # Local secrets (gitignored)
├── .env.example                            # Template for new developers
├── docker-compose.yml
└── Dockerfile
```

## Development Guidelines

### Code Style

- Follow Eclipse formatter configuration (`eclipse-formatter.xml`)
- Use Spotless for code formatting: `mvn spotless:apply`
- Check code formatting: `mvn spotless:check`
- Maximum line length: 120 characters

### Testing

```bash
# Run unit tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Deployment

### Production Build

```powershell
# Build production JAR
mvn clean package -Pproduction

# Kill any running Java process first
taskkill /F /IM java.exe

# Then rebuild
mvn clean package -DskipTests -Pproduction

# Run the JAR
java -jar target/cl-booster-1.0-SNAPSHOT.jar
```

### Docker Image

```bash
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

### Test Gemini API Key

```powershell
curl -s "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=YOUR_KEY" `
  -H "Content-Type: application/json" `
  -d '{"contents":[{"parts":[{"text":"Hello"}]}]}'
```
