# CL Booster - AI-Powered Cover Letter Generator

[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5+-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vaadin](https://img.shields.io/badge/Vaadin-24.9+-blue.svg)](https://vaadin.com/)

CL Booster is an intelligent cover letter generation tool that leverages AI to create personalized, professional cover letters tailored to each job application.

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Maven 3.6+
- Docker & Docker Compose
- Google Gemini API key ([get one here](https://aistudio.google.com))

### Local Development

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd cl-booster
   ```

2. **Configure environment**

   ```bash
   cp .env.example .env
   # Edit .env — add your GEMINI_API_KEY and GOOGLE_PROJECT_ID
   ```

3. **Run**

   ```powershell
   mvn spring-boot:run -DskipTests
   ```

4. **Access the application**
   - Open http://localhost:8080 in your browser

## ✨ Features

- **5-step wizard** — job details → resume import → skills → summary → inline editor
- **AI generation** — Google Gemini writes personalized cover letters using your profile + resume
- **3 tone styles** — Professional, Creative, Storyteller
- **Resume manager** — upload, preview, download, delete resumes
- **History** — browse, re-edit, and export past cover letters
- **Multi-language UI** — English, Finnish, Swedish, German, French
- **Export** — DOCX and PDF from the editor

## 🏗️ Architecture

### Backend

- **Spring Boot 3.5+** — application framework
- **Vaadin Flow 24.9+** — server-side Java UI
- **Spring AI** — Google Gemini integration
- **MariaDB** — database infrastructure prepared (uncomment in `application.properties` to activate)
- **File storage** — `uploads/` directory for resumes and cover letters

### AI Pipeline

1. User profile fetched from `ProfileDAO`
2. Latest uploaded resume parsed by `Parser`
3. Combined context sent to `AIService` → Gemini API
4. Result rendered in inline step-5 editor
5. Auto-saved as `.docx` to `uploads/coverletters/`

### DevOps

- **Docker** — containerization
- **GitHub Actions** — CI/CD pipeline
- **Maven** — build automation
- **spring-dotenv** — `.env` file support

## 📁 Project Structure

```
cl-booster/
├── src/main/java/com/clbooster/
│   ├── aiservice/                          # AIService, Exporter, Parser
│   └── app/
│       ├── Application.java
│       ├── backend/
│       │   ├── config/                     # SecurityConfig, AiConfig
│       │   └── service/
│       │       ├── authentication/         # AuthenticationService
│       │       ├── database/               # UserDAO, SQL scripts
│       │       ├── document/               # DocumentService
│       │       └── profile/                # ProfileService, UserService, User
│       └── views/                          # All Vaadin views
├── src/main/resources/
│   ├── application.properties
│   └── i18n/                               # en, fi, sv, de, fr translations
├── uploads/                                # Runtime file storage (gitignored)
├── .env                                    # Local secrets (gitignored)
└── .env.example                            # Template
```

## 🔧 Development

See [dev_instructions.md](dev_instructions.md) for full setup, CLI usage, Docker, and deployment details.

## 📄 License

This project is licensed under the MIT License — see [LICENSE.md](LICENSE.md) for details.

## 🙏 Acknowledgments

- [Vaadin](https://vaadin.com/) for the excellent web framework
- [Spring Boot](https://spring.io/projects/spring-boot) for the application framework
- [Google Gemini](https://ai.google.dev/) for the AI capabilities

---

**Made with ❤️ using Vaadin + Spring Boot**
