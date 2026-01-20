# CL Booster - AI-Powered Cover Letter Generator

[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5+-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vaadin](https://img.shields.io/badge/Vaadin-24.9+-blue.svg)](https://vaadin.com/)

CL Booster is an intelligent cover letter generation tool that leverages AI to create personalized, professional cover letters

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven 3.6+
- Docker & Docker Compose
- Google Gemini API key

### Local Development

1. **Clone the repository**

   ```bash
   git clone ...
   cd cl-booster
   ```

2. **Configure environment**

   ```bash
   cp .env.example .env
   # Edit .env with your Gemini API key
   ```

3. **Start with Docker Compose**

   ```bash
   docker-compose up -d
   ```

4. **Access the application**
   - Open http://localhost:8080 in your browser

### Manual Setup

1. **Build and run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## 🏗️ Architecture

### Backend

- **Spring Boot 3.5+**: Application framework
- **Vaadin Flow 24.9+**: Modern web UI framework
- **Spring AI**: Google Gemini integration
- **MariaDB**: Database infrastructure prepared (uncomment to activate)
- **In-memory Storage**: Current data persistence (upgradeable to database)

### Frontend

- **Vaadin Components**: Rich UI components

### DevOps

- **Docker**: Containerization
- **GitHub Actions**: CI/CD pipeline
- **Maven**: Build automation
- **Spotless**: Code formatting

## 📁 Project Structure

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

## 🔧 Development

See [dev_instructions.md](dev_instructions.md) for detailed development setup and guidelines.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 🙏 Acknowledgments

- [Vaadin](https://vaadin.com/) for the excellent web framework
- [Spring Boot](https://spring.io/projects/spring-boot) for the application framework
- [Google Gemini](https://ai.google.dev/) for the AI capabilities

---

**Made with ❤️ with Vaadin**
