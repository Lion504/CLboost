# Sprint 3 Review Report

## Sprint Goal

1. Extend functional prototype with complete UI and backend integration
2. Integrate Jenkins for CI/CD pipeline
3. Enhance automated testing with code coverage reporting
4. Develop and test Docker image locally
5. Connect Register/Login UI to backend authentication API
6. Ensure app is demo-ready for functional review

## Completed User Stories / Tasks

- CLGEN-74: Integrate Scan API to scan the user's resume
- CLGEN-40: Automated Unit and Coverage testing
- CLGEN-38: Integrate backend API with frontend UI
- CLGEN-14: Connect Register and Login UI pages to backend authentication API
- CLGEN-9: Complete interactive frontend UI
- CLGEN-39: Integrate Jenkins for CI/CD
- CLGEN-41: Functional review app is demo-ready
- CLGEN-86: Add a diagram directory on the documentation folder
- CLGEN-122: Create remaining DAO and services
- CLGEN-42: Develop and test the Docker image in the local machine

## Completed Tasks Detail

### 1. ✅ Extend Functional Prototype (4 points)

- **Feature Implementation:**
  - Complete graphical user interface with Vaadin framework and database interaction
  - User authentication and authorization module with Login/Register flow
  - Core features from initial product vision and backlog
  - Resume upload, scanning, and cover letter generation workflows

- **Core Functionality Validation:**
  - Performance testing to identify and address bottlenecks
  - Unit tests for all functions with comprehensive coverage

- **Bug Fixing and Optimization:**
  - Addressed logic bugs identified during testing
  - Optimized backend performance for improved responsiveness

### 2. ✅ Integrate Jenkins for CI/CD (5 Points)

- **Pipeline Configuration:**
  - Automatic build and test upon code commits to main branch
  - **Stages:** Code Checkout → Build (Maven) → Unit Tests (JUnit) → Code Coverage (JaCoCo)
  - Jenkinsfile created and configured

### 3. ✅ Automated Unit & Coverage Testing

- **Test Suite Extension:**
  - Unit tests for newly implemented features with focus on edge cases
  - Tests for Authentication, Profile, Resume parsing, and Document services

- **JaCoCo Integration:**
  - Jenkins pipeline generates and publishes JaCoCo HTML coverage reports
  - Coverage reports accessible to the team

### 4. ✅ Functional Review Readiness

- **End-to-End Functionality:**
  - Complete user workflows from registration to cover letter generation
  - Backend and frontend fully integrated and functional

- **Feature Completeness:**
  - All main features can be demonstrated
  - Demo-ready application state

### 5. ✅ Develop and Test Docker Image (1 point)

- Dockerfile created and configured
- Docker Compose setup for local development and testing
- Successfully tested on Docker Desktop locally

### 6. ✅ Prepare for Sprint Review

- **Demonstration Preparation:**
  - Working prototype demonstration highlighting extended features
  - Jenkins CI/CD pipeline demonstration
  - Code coverage report presentation
  - Docker image functionality demonstration

- **Documentation Updates:**
  - GitHub repository updated with latest code and documentation
  - ER Model and Relational Schema diagrams added to documentation/diagrams/
  - Jira board updated with task completion status

## Demo Summary

During the sprint review, we demonstrated:

<<<<<<< HEAD

- **Backend Command Line Interface** - Resume scanning and cover letter generation
- **Code Coverage Reports** - JaCoCo HTML reports showing test coverage
- **Unit Tests** - JUnit test execution results
- **Jenkins CI/CD Pipeline** - Automated build, test, and report generation
- **Docker Image** - Working Docker container running the application locally
- **Frontend UI Prototype** - Complete Vaadin-based interactive UI

- **Entity Relation Diagram (Database ER Model)**
  - Located in: `documentation/diagrams/ER Model.png`
  - Shows database structure for Users, Profiles, Cover Letters, and Settings

- **Relational Schema**
  - Located in: `documentation/diagrams/Relational Schema.png`

- # **Jira Board:** https://taysa-ferreira-abinader.atlassian.net/jira/software/projects/CLGEN/boards/67/backlog
- Demonstrated functional application
- Presented the Docker Image for the application
- Presented the tests and code coverage report
- Showed the Frontend Figma Prototype:
  https://www.figma.com/proto/LFr0EooJ9Nmnfztwv4pO3F/CLbooster?node-id=4-2492&p=f&t=vZzS39YYiB2ldEY4-1&scaling=min-zoom&content-scaling=fixed&page-id=0%3A1&starting-point-node-id=4%3A2492
- Presented the Sprint 3 backlog: https://taysa-ferreira-abinader.atlassian.net/jira/software/projects/CLGEN/boards/67/backlog

## What Went Well

- Excellent support among team members
- Successful Jenkins CI/CD pipeline implementation
- Docker image builds and runs successfully
- Backend and frontend integration working end-to-end
- Test coverage improved with JaCoCo integration

## What Could Be Improved

- There is still some room for improvement in communication
- Some UI refinements could be addressed in the next sprint
- Additional edge case testing could strengthen coverage

## Next Sprint Focus

- Final UI polish and refinements based on Figma prototype
- End-to-end integration testing of complete workflows
- Performance optimization
- Final bug fixes and stability improvements
- Documentation finalization

## Time Tracking

<<<<<<< HEAD
| Team Member | Hours Spent | Tasks Contributions |
| ----------------- | ----------- | ------------------------------------------ |
| Wang Yongzhi | 60 | Scrum Master, frontend building |
| Taysa Abinader | | Jenkins CI/CD setup, automated testing |
| Tamseela Mahmood | | DAO and Services creation, database models |
| Kiavash Montazeri | | Docker image development and testing |
=======
| Team Member | Hours Spent | Tasks Contributions |
| ----------------- |---| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Wang Yongzhi | | |
| Taysa Abinader | | |
| Tamseela Mahmood | 4 | worked on remaining dao and services functionalities and tests |
| Kiavash Montazeri | | |

> > > > > > > c0d8fc43816b53c488b228f5f3e8376df50666c0

**Total Sprint Hours:**
