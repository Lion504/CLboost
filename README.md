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

Urdu (اردو) and Persian (فارسی) use **right-to-left (RTL)** text direction. The application handles RTL through three layers:

### How RTL Works

1. **`dir` attribute** — `TranslationService.setCurrentLocale()` sets `dir="rtl"` on the `<html>` element whenever an RTL language (fa, ur, ar, he) is selected
2. **Vaadin auto-flip** — Vaadin Flow 24.9+ reads `dir="rtl"` and automatically flips `AppLayout` drawer position, navbar order, and component flow direction
3. **Custom CSS overrides** — `styles.css` adds RTL-specific rules for cards, forms, navigation items, toggle switches, and animations

### What Flips Automatically

- Sidebar drawer position (left ↔ right)
- Form label/input alignment
- Navigation menu order
- Table column order
- Button group ordering
- Toggle switch thumb position (uses CSS `inset-inline-start/end`)

### RTL Font Fallbacks

RTL languages use Arabic-script fonts with cascading fallbacks: `Noto Sans Arabic` → `Noto Nastaliq Urdu` → `Vazirmatn` → system defaults.

### Testing RTL

1. Launch the application
2. Open **Settings → Language**
3. Select **فارسی** (Persian) or **اردو** (Urdu)
4. Verify: sidebar moves right, all text aligns right, toggle switches flip, no overlapping or clipped content
5. Switch back to English — verify everything flips back to LTR

## 🧪 Testing & Code Quality

### SonarQube (Static Code Analysis)

SonarQube provides continuous inspection of code quality, security, and maintainability.

#### Quick Start with Docker

```bash
# Start SonarQube container (runs on http://localhost:9000)
docker run -d --name sonarqube \
  -p 9000:9000 \
  sonarqube:lts-community

# Wait ~2 minutes for startup, then access:
# http://localhost:9000 | admin / admin
```

#### Linux Installation (Manual / Systemd)

# 1. Start SonarQube

sudo -u sonar /opt/sonarqube/bin/linux-x86-64/sonar.sh start

# Check logs on first run

sudo -u sonar /opt/sonarqube/bin/linux-x86-64/sonar.sh status
tail -f /opt/sonarqube/logs/sonar.log

# 2. Access at http://localhost:9000

# Default credentials: admin / admin (change on first login)

#### Run Analysis

```bash
# Full build with tests + coverage + Sonar analysis
mvn clean verify sonar:sonar

# Or run analysis only (skip tests)
mvn compile sonar:sonar

# Or
mvn clean jacoco:prepare-agent test jacoco:report sonar:sonar -Dmaven.test.failure.ignore=true
```

## 🚀 Performance Testing

CLboost includes an Apache JMeter test plan for load testing the web application, helping evaluate responsiveness and stability under concurrent user load.

### Test Plan Location

`tests/performance/clboost_performance.jmx`

### Test Scenarios

- Simulates **10 concurrent users** performing typical navigation (landing, login, dashboard, editor, history, etc.)
- Each user performs **5 iterations** with a **2-second think time** between requests.
- Both public and protected pages are tested; protected pages return redirects for anonymous users (expected).

### Running Locally

1. **Install JMeter** 5.6.3+ and ensure `jmeter` is on your `PATH`.  
   Download: https://jmeter.apache.org/download_jmeter.cgi

2. **Start the application** (Docker or `mvn spring-boot:run`) on port `8080` (or adjust).

3. **Execute the test:**

```bash
jmeter -n -t tests/performance/clboost_performance.jmx -l result.jtl -Jport=8080 -e -o report/
```

1. **View the report:** Open `report/index.html` in a browser.

### CI Integration

Performance tests are part of the Jenkins pipeline (`Jenkinsfile`). The **Performance Test** stage runs automatically on every build against a running test environment. Results are archived as build artifacts.

### Interpreting Results

- **Avg** – average response time (ms). Aim for < 2000ms.
- **Err %** – error rate. Should be ≤ 5% (5xx responses or assertion failures).
- **Throughput** – requests/second; higher is better.

### Current Limitations

JMeter simulates HTTP requests only; it does not execute JavaScript or Vaadin client-side interactions. Tests focus on server response times for page bootstrap. For full browser-level performance, consider Vaadin TestBench with Selenium Grid.

---

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
