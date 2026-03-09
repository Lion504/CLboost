# Quick Start: Testing Resume Scan Features

## Run All Tests

```bash
mvn test
```

## Run Specific Feature Tests

### Test Resume Scanning
```bash
mvn test -Dtest=AIServiceTest
```

### Test Resume Upload & Management
```bash
mvn test -Dtest=ResumeServiceTest
```

### Test Document Storage & Export  
```bash
mvn test -Dtest=DocumentServiceTest
```

### Test Complete Workflow
```bash
mvn test -Dtest=ResumeWorkflowIntegrationTest
```

## Run with Coverage Report

```bash
mvn test jacoco:report
# Open report: target/site/jacoco/index.html
```

## Manual UI Testing

### 1. Start the Application
```bash
mvn spring-boot:run
```

### 2. Navigate to Resume Manager
- Go to: `http://localhost:8080/resumes`
- Or click "Resume Manager" in the navigation menu

### 3. Test Upload (Sample Files Available)
- Use file: `src/test/resources/sample_resume.txt`
- Observe AI scanning (3-5 seconds)
- Review extracted data in the form

### 4. Test Approval Workflow
- Edit any fields as needed
- Click "Approve & Save"
- Confirm the dialog
- Should see success notification

## Test Scenarios

### Scenario 1: Basic Resume Upload & Scan ✓
- **File:** `sample_resume.txt`
- **Expected:** AI extracts name, email, skills, experience
- **Verify:** All fields populate in review form

### Scenario 2: Edit and Approve ✓
- **Action:** Modify extracted data and click save
- **Expected:** Resume saves to user profile
- **Verify:** Success notification appears

### Scenario 3: Invalid File ✓
- **File:** Any file with `.xyz` extension
- **Expected:** Error notification
- **Verify:** "Unsupported file format" message

### Scenario 4: Empty File ✓
- **File:** Empty text file
- **Expected:** Error notification
- **Verify:** "Resume file cannot be empty" message

### Scenario 5: Job Matching ✓
- **File:** `sample_resume.txt`
- **Job Desc:** `sample_job_posting.txt` content
- **Expected:** AI identifies relevant qualifications
- **Verify:** Key points related to Java, Spring Boot, Microservices

## Key Test Files Created

| File | Purpose |
|------|---------|
| `AIServiceTest.java` | Unit tests for AI resume scanning |
| `ResumeServiceTest.java` | Unit tests for resume upload/management |
| `DocumentServiceTest.java` | Unit tests for document storage/export |
| `ResumeWorkflowIntegrationTest.java` | Integration tests for complete workflows |
| `sample_resume.txt` | Test data - sample resume |
| `sample_job_posting.txt` | Test data - sample job posting |

## Troubleshooting

**Tests fail with "ChatModel not found"**
- Ensure Gemini API key is configured
- Check `application.properties` for `spring.ai.vertex.ai.gemini.api-key`

**Tests timeout**
- Increase timeout in test: `@Test(timeout = 10000)`
- Check network connection to Gemini API

**File upload spins indefinitely**
- Check file size (limit: 10MB)
- Verify temp directory is writable

## Debug Mode

Enable detailed logging:

```properties
# In application-test.properties or application.properties
logging.level.com.clbooster=DEBUG
logging.level.org.springframework.ai=DEBUG
```

Then run:
```bash
mvn test -Dspring.profiles.active=debug
```

## Expected Test Results

All tests should pass with output similar to:
```
[INFO] Tests run: 33, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Code Coverage Target: **> 80%** for service classes

## Next Steps

1. ✅ Run all tests to verify implementation
2. ✅ Check coverage report at `target/site/jacoco/`
3. ✅ Perform manual UI testing in browser
4. ⬜ Integrate with database (replace in-memory cache)
5. ⬜ Add authentication for real user PINs
6. ⬜ Add job description input form
7. ⬜ Create resume version management

---

**For detailed testing information, see:** [TESTING_GUIDE.md](TESTING_GUIDE.md)
