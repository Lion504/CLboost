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

### Quick Start (Docker Hub)

**Option A - Docker Compose (recommended):**

```bash
git clone <repository-url>
cd cl-booster
cp .env.example .env
# Edit .env — add your GEMINI_API_KEY and GOOGLE_PROJECT_ID
docker-compose up -d
```

**Option B - Pull from Docker Hub:**

```bash
docker pull timo2233/clboost:v1.0.4
docker run -d -p 8080:8080 --name clboost-app \
  -e GEMINI_API_KEY=your_api_key \
  -e DB_HOST=db \
  -e DB_PORT=3306 \
  -e DB_NAME=CL_generator \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  timo2233/clboost:v1.0.4
```

**Option C - Build locally:**

```bash
docker build -t clboost-app .
docker run -d -p 8080:8080 --name clboost-app \
  -e GEMINI_API_KEY=your_api_key \
  -e DB_HOST=db \
  -e DB_PORT=3306 \
  -e DB_NAME=CL_generator \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  clboost-app
```

Access the application at http://localhost:8080

## ✨ Features

- **5-step wizard** — job details → resume import → skills → summary → inline editor
- **AI generation** — Google Gemini writes personalized cover letters using your profile + resume
- **3 tone styles** — Professional, Creative, Storyteller
- **Resume manager** — upload, preview, download, delete resumes
- **History** — browse, re-edit, and export past cover letters
- **Multi-language UI** — English, Finnish, Chinese, Urdu, Persian, Portuguese
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
│   └── messages*.properties                # en, fi, zh, ur, fa, pt translations
├── uploads/                                # Runtime file storage (gitignored)
├── .env                                    # Local secrets (gitignored)
└── .env.example                            # Template
```

## 🌐 Localization Framework

CL Booster uses a **Java `ResourceBundle`-based localization system** integrated with **Vaadin's `I18NProvider`** interface for seamless server-side UI translation.

### Architecture

```
UI Views (DashboardView, HistoryView, etc.)
         │ translate("key", params)
         ▼
TranslationService.java (implements I18NProvider)
  - Resolves locale: session → user settings → default
  - Loads ResourceBundle for the active locale
  - Falls back to English if a key is missing
  - Formats {0}, {1} placeholders via MessageFormat
         │ ResourceBundle.getBundle()
         ▼
messages*.properties  (en, fi, zh, ur, fa, pt)
```

### Supported Languages

| Language   | Code | File                     | Script    | Direction |
| ---------- | ---- | ------------------------ | --------- | --------- |
| English    | `en` | `messages.properties`    | Latin     | LTR       |
| Finnish    | `fi` | `messages_fi.properties` | Latin     | LTR       |
| Portuguese | `pt` | `messages_pt.properties` | Latin     | LTR       |
| Chinese    | `zh` | `messages_zh.properties` | Han (CJK) | LTR       |
| Urdu       | `ur` | `messages_ur.properties` | Arabic    | RTL       |
| Persian    | `fa` | `messages_fa.properties` | Arabic    | RTL       |

## 🔀 RTL Support

Urdu (اردو) and Persian (فارسی) use **right-to-left (RTL)** text direction. Vaadin Flow 24.9+ automatically applies `dir="rtl"` on the `<html>` element when an RTL locale is active, flipping layout direction for all standard components.

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
