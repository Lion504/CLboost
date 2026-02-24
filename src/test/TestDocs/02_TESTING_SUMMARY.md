# Testing Overview - Resume Scan Features

## Summary of Testing Files Created

### Test Source Files (8 Files)

| File | Tests | Purpose |
|------|-------|---------|
| `src/test/java/.../AIServiceTest.java` | 4 | AI resume scanning functionality |
| `src/test/java/.../ResumeServiceTest.java` | 12 | Resume upload and management |
| `src/test/java/.../DocumentServiceTest.java` | 9 | Document storage and export |
| `src/test/java/.../ResumeWorkflowIntegrationTest.java` | 8 | End-to-end workflow tests |
| **Total Unit Tests** | **33** | **Comprehensive coverage** |

### Test Data Files (2 Files)

| File | Content | Usage |
|------|---------|-------|
| `src/test/resources/sample_resume.txt` | Real-world resume example | Manual and automated testing |
| `src/test/resources/sample_job_posting.txt` | Job description | Job matching tests |

---

## Test Coverage by Feature

```
┌──────────────────────────────────────────┐
│  Resume Scanning & AI Integration        │
├──────────────────────────────────────────┤
│ ✓ Extract structured resume data         │
│ ✓ Parse JSON responses from Gemini AI    │
│ ✓ Handle empty/invalid input             │
│ ✓ Analyze job-resume matching            │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│  Resume Upload & File Management         │
├──────────────────────────────────────────┤
│ ✓ Accept TXT, PDF, DOCX formats          │
│ ✓ Validate file size and format          │
│ ✓ Reject empty files                     │
│ ✓ Extract text from various formats      │
│ ✓ Handle file upload errors              │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│  Resume Data Management                  │
├──────────────────────────────────────────┤
│ ✓ Save resume to user profile            │
│ ✓ Retrieve saved resumes                 │
│ ✓ Cache in-memory storage                │
│ ✓ Clear cache on demand                  │
│ ✓ Handle multiple user resumes           │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│  Document Storage & Export               │
├──────────────────────────────────────────┤
│ ✓ Store uploaded files                   │
│ ✓ Retrieve stored files                  │
│ ✓ Export resume as DOCX                  │
│ ✓ Delete stored files                    │
│ ✓ Format resume with all sections        │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│  UI Form & Workflow                      │
├──────────────────────────────────────────┤
│ ✓ Upload component accepts files         │
│ ✓ Review form displays extracted data    │
│ ✓ Edit fields before approval            │
│ ✓ Approve and save functionality         │
│ ✓ Re-upload for new resume               │
│ ✓ Error notifications                    │
│ ✓ Success notifications                  │
└──────────────────────────────────────────┘
```

---

## Running Tests

### Quick Commands

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ResumeServiceTest

# Run with coverage
mvn test jacoco:report

# Run integration tests
mvn verify -Pit
```

### Expected Output

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.clbooster.app.backend.service.ai.AIServiceTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0

[INFO] Running com.clbooster.app.backend.service.ResumeServiceTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

[INFO] Running com.clbooster.app.backend.service.document.DocumentServiceTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0

[INFO] Running com.clbooster.app.backend.service.ResumeWorkflowIntegrationTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0

[INFO] -------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] Total time: 45.123 s
[INFO] -------------------------------------------------------
```

---

## Manual Testing Flow

### Access Application
1. **Start:** `mvn spring-boot:run`
2. **Open:** `http://localhost:8080/resumes`

### Step-by-Step Manual Test

```
Step 1: Upload Resume
  └─ Click "Upload Your Resume"
  └─ Select: src/test/resources/sample_resume.txt
  └─ Observe: "Scanning resume..." notification

Step 2: AI Processing
  └─ Wait 3-5 seconds
  └─ Observe: "Resume scanned successfully!" notification

Step 3: Review Form Appears
  └─ Verify: Personal info populated
  └─ Verify: Skills listed
  └─ Verify: Experience populated
  └─ Verify: Education listed
  └─ Verify: All fields are editable TextFields/TextAreas

Step 4: Edit Data (Optional)
  └─ Modify any field as needed
  └─ Example: Add space to full name

Step 5: Approve & Save
  └─ Click "Approve & Save" button
  └─ Confirmation dialog appears
  └─ Click "Yes, Save"
  └─ Observe: "Resume saved successfully!" notification

Step 6: Form Resets
  └─ Review section hides
  └─ Upload section resets for new resume
  └─ Ready for next upload
```

---

## Test Execution Timeline

| Phase | Duration | Tests | Details |
|-------|----------|-------|---------|
| Unit Tests | ~15s | 25 | Service layer functions |
| Integration Tests | ~20s | 8 | Complete workflows |
| Total | ~35s | 33 | Full test suite |

---

## Coverage Report

After running: `mvn test jacoco:report`

**Open:** `target/site/jacoco/index.html`

Expected Coverage:
- **AIService:** > 90%
- **ResumeService:** > 85%
- **DocumentService:** > 80%
- **Overall:** > 85%

---

## Critical Test Cases

### Must Pass ✓

1. **Resume Upload (Valid Format)**
   - Upload TXT file → AI scans → Data displays ✓

2. **Form Review**
   - All fields display correctly ✓
   - All fields are editable ✓

3. **Data Persistence**
   - Save resume → Retrieve resume → Data intact ✓

4. **Error Handling**
   - Invalid format → Error message ✓
   - Empty file → Error message ✓

5. **Export to DOCX**
   - Resume exports successfully ✓
   - File is readable in Word ✓

---

## Troubleshooting Tests

### Problem: ChatModel Bean Not Found
```
Solution: Ensure spring-ai-vertex-ai-gemini is in classpath (check pom.xml)
and Gemini API key is configured
```

### Problem: File Not Found in Tests
```
Solution: Ensure src/test/resources/ directory exists
and sample files are in correct location
```

### Problem: Tests Timeout
```
Solution: Increase Spring context startup time or 
disable auto-configuration for specific tests
```

### Problem: Out of Memory
```
Solution: Clear cache between tests in @BeforeEach:
resumeService.clearCache()
```

---

## Continuous Integration Setup

For CI/CD pipeline:

```yaml
# .github/workflows/test.yml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '25'
      - run: mvn clean test jacoco:report
      - uses: codecov/codecov-action@v2
        with:
          files: ./target/site/jacoco/jacoco.xml
```

---

## Performance Benchmarks

Expected Performance:
| Operation | Time | Limit |
|-----------|------|-------|
| Resume Scan (AI) | 3-5s | < 10s |
| File Upload | < 1s | < 5s |
| Save to Profile | < 100ms | < 500ms |
| Document Export | < 2s | < 5s |

---

## Validation Checklist

Run through these before deployment:

- [ ] All 33 tests pass
- [ ] Coverage > 85%
- [ ] No warnings in logs
- [ ] Upload accepts TXT files
- [ ] Upload rejects invalid formats
- [ ] AI scanning displays results
- [ ] Form fields are editable
- [ ] Approve button saves data
- [ ] Re-upload clears form
- [ ] Export creates valid DOCX

---

**All tests created and ready to run. See 01_QUICK_TEST.md for immediate commands.**
