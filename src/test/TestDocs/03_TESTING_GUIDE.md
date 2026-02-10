# Comprehensive Testing Guide - Resume Scan Features

## Table of Contents
1. [Unit Tests](#unit-tests)
2. [Integration Tests](#integration-tests)
3. [Manual Testing](#manual-testing)
4. [Test Data](#test-data)
5. [Best Practices](#best-practices)

---

## Unit Tests

### AIService Tests

**Location:** `src/test/java/com/clbooster/app/backend/service/ai/AIServiceTest.java`

#### Test: Resume Scanning
```java
@Test
@DisplayName("Should scan resume and extract structured data")
void testScanResume() {
    ResumeData result = aiService.scanResume(sampleResumeText);
    
    assertNotNull(result);
    assertNotNull(result.getRawResumeText());
    assertEquals(sampleResumeText, result.getRawResumeText());
}
```

**Coverage:**
- Input validation
- AI API call
- JSON response parsing
- ResumeData object creation

#### Test: Job Matching
```java
@Test
@DisplayName("Should analyze job match and return selling points")
void testAnalyzeJobMatch() {
    List<String> matchPoints = aiService.analyzeJobMatch(
        sampleResumeText, 
        jobDescription
    );
    
    assertNotNull(matchPoints);
    assertTrue(matchPoints.size() <= 5);
}
```

**Coverage:**
- Job description processing
- Qualification extraction
- Relevance analysis

---

### ResumeService Tests

**Location:** `src/test/java/com/clbooster/app/backend/service/ResumeServiceTest.java`

#### Test: File Upload Validation
```java
@Test
@DisplayName("Should validate file format - reject invalid")
void testUploadAndScanResume_InvalidFormat() {
    MultipartFile invalidFile = new MockMultipartFile(
        "file",
        "resume.xyz",
        "application/xyz",
        content
    );
    
    assertThrows(IllegalArgumentException.class, () -> {
        resumeService.uploadAndScanResume(invalidFile);
    });
}
```

**Coverage:**
- Format validation (TXT, PDF, DOCX, XYZ)
- Error messaging
- User feedback

#### Test: Resume Persistence
```java
@Test
@DisplayName("Should save and retrieve resume from profile")
void testSaveAndGetResume() {
    ResumeData resumeData = new ResumeData();
    resumeData.setFullName("Jane Smith");
    
    int pin = 12345;
    resumeService.saveResumeToProfile(pin, resumeData);
    ResumeData retrieved = resumeService.getResumeForProfile(pin);
    
    assertNotNull(retrieved);
    assertEquals("Jane Smith", retrieved.getFullName());
}
```

**Coverage:**
- Data persistence
- Cache operations
- Data retrieval
- Data integrity

---

### DocumentService Tests

**Location:** `src/test/java/com/clbooster/app/backend/service/document/DocumentServiceTest.java`

#### Test: File Storage
```java
@Test
@DisplayName("Should store resume file successfully")
void testStoreResumeFile() throws IOException {
    MultipartFile file = new MockMultipartFile(
        "file",
        "resume.txt",
        "text/plain",
        content
    );
    
    String storagePath = documentService.storeResumeFile(file, "user123");
    
    assertNotNull(storagePath);
    assertTrue(Files.exists(Paths.get(storagePath)));
}
```

**Coverage:**
- File I/O operations
- Path generation
- File system verification

#### Test: DOCX Export
```java
@Test
@DisplayName("Should export resume as DOCX document")
void testExportResumeAsDocument() {
    ResumeData resumeData = new ResumeData();
    resumeData.setFullName("John Doe");
    resumeData.getSkills().add("Java");
    
    String outputPath = "test_output/resume.docx";
    boolean success = documentService.exportResumeAsDocument(
        resumeData, 
        outputPath
    );
    
    assertTrue(success);
    assertTrue(Files.exists(Paths.get(outputPath)));
}
```

**Coverage:**
- Document formatting
- Section organization
- File export
- Apache POI integration

---

## Integration Tests

### Complete Workflow Test

**Location:** `src/test/java/com/clbooster/app/backend/service/ResumeWorkflowIntegrationTest.java`

#### Test: End-to-End Upload → Scan → Save
```java
@Test
@DisplayName("Complete workflow: Upload -> Scan -> Review -> Save")
void testCompleteResumeScanWorkflow() throws IOException {
    // Step 1: Upload file
    byte[] resumeBytes = sampleResumeText.getBytes(StandardCharsets.UTF_8);
    MultipartFile resumeFile = new MockMultipartFile(
        "file",
        "resume.txt",
        "text/plain",
        resumeBytes
    );
    
    // Step 2: Scan with AI
    ResumeData scannedData = resumeService.uploadAndScanResume(resumeFile);
    assertNotNull(scannedData);
    assertTrue(scannedData.getRawResumeText().contains("John"));
    
    // Step 3: Save to profile
    int userPin = 12345;
    boolean saved = resumeService.saveResumeToProfile(userPin, scannedData);
    assertTrue(saved);
    
    // Step 4: Verify retrieval
    ResumeData retrieved = resumeService.getResumeForProfile(userPin);
    assertNotNull(retrieved);
    assertEquals(scannedData.getRawResumeText(), retrieved.getRawResumeText());
}
```

**Coverage:**
- File upload handling
- AI processing pipeline
- Data transformation
- Persistence layer
- Retrieval verification

---

## Manual Testing

### Scenario 1: Happy Path

**Objective:** Verify complete user workflow from upload to approval

**Steps:**
1. Navigate to `http://localhost:8080/resumes`
2. Click "Upload Your Resume"
3. Select `src/test/resources/sample_resume.txt`
4. Wait for "Scanning resume..." notification
5. Wait 3-5 seconds for AI processing
6. See "Resume scanned successfully!" notification
7. Verify form displays:
   - Full Name: "John Anderson"
   - Email: Contains "@"
   - Phone: Contains digits
   - Skills: Multiple entries visible
   - Work Experience: Job titles and companies displayed
   - Education: Degrees listed
   - Certifications: Professional certs shown

**Expected Result:** ✓ All fields populated and editable

---

### Scenario 2: User Edits & Approves

**Objective:** Verify user can modify and approve extracted data

**Steps:**
1. Complete Scenario 1
2. Edit Full Name field (add middle initial)
3. Add one skill to Skills field
4. Click "Approve & Save"
5. Confirmation dialog appears
6. Click "Yes, Save"
7. Observe form resets

**Expected Result:** 
- ✓ "Resume saved successfully!" notification
- ✓ Review section hides
- ✓ Upload section ready for new resume

---

### Scenario 3: Invalid Format Handling

**Objective:** Verify error handling for invalid file formats

**Steps:**
1. Create file: `test.xyz` with any content
2. Try to upload to Resume Manager
3. Observe error notification

**Expected Result:** 
- ✓ Error message: "Unsupported file format. Please upload PDF, DOCX, or TXT files."
- ✓ File not processed
- ✓ Form remains on upload screen

---

### Scenario 4: Empty File Handling

**Objective:** Verify empty files are rejected

**Steps:**
1. Create empty text file
2. Try to upload
3. Observe error

**Expected Result:** 
- ✓ Error message: "Resume file cannot be empty"
- ✓ No processing attempted

---

### Scenario 5: Job Matching (if implemented)

**Objective:** Verify job description analysis

**Steps:**
1. Upload resume
2. Input `src/test/resources/sample_job_posting.txt` content
3. Click "Match with Job"
4. Observe results

**Expected Result:** 
- ✓ Top 5 relevant qualifications displayed
- ✓ Keywords match job requirements (Java, Spring Boot, Microservices)
- ✓ Percentage match shown

---

## Test Data

### Sample Resume (`sample_resume.txt`)

```
JOHN ANDERSON
john.anderson@email.com
(555) 987-6543

Professional Summary:
Results-driven Software Engineer with 6+ years experience.

Skills: Java, Spring Boot, Microservices, SQL, Docker, AWS

Work Experience:
Senior Engineer - TechXYZ Solutions (March 2022 - Present)
- Architected microservices platform
- Led team of 5 developers

Education:
BS Computer Science - UC Berkeley (2018)

Certifications:
AWS Certified Solutions Architect
```

**Format:** Plain text with clear sections  
**Purpose:** Tests all extraction capabilities  
**Usage:** Automated and manual testing  

---

### Sample Job Posting (`sample_job_posting.txt`)

```
Senior Java Backend Engineer

Requirements:
- 5+ years Java development
- Spring Boot experience
- Microservices architecture
- SQL and MongoDB
- Docker and Kubernetes
- AWS cloud platform
- CI/CD pipeline experience

Responsibilities:
- Design REST APIs
- Build scalable systems
- Lead engineering team
- Mentor junior developers
```

**Format:** Structured job description  
**Purpose:** Tests job matching algorithm  
**Usage:** Job analysis tests  

---

## Best Practices

### Writing Tests

1. **Use Descriptive Names**
   ```java
   @DisplayName("Should scan resume and extract structured data")
   void testScanResume() { ... }
   ```

2. **Follow Arrange-Act-Assert Pattern**
   ```java
   // Arrange
   ResumeData resumeData = new ResumeData();
   
   // Act
   boolean saved = service.saveResumeToProfile(pin, resumeData);
   
   // Assert
   assertTrue(saved);
   ```

3. **Test Both Happy Path and Error Cases**
   ```java
   // Happy path
   void testValidOperation() { ... }
   
   // Error case
   void testInvalidInput() { ... }
   ```

4. **Use Meaningful Assertions**
   ```java
   // Good
   assertEquals("expected", actual, "Description of what should happen");
   
   // Poor
   assertTrue(result);
   ```

### Running Tests Efficiently

1. **Run related tests together**
   ```bash
   mvn test -Dtest=ResumeServiceTest
   ```

2. **Generate coverage during test**
   ```bash
   mvn test jacoco:report
   ```

3. **Run single test for debugging**
   ```bash
   mvn test -Dtest=ResumeServiceTest#testSaveResumeToProfile
   ```

### Debugging Failed Tests

1. **Enable debug logging**
   ```bash
   mvn test -Dspring.profiles.active=debug
   ```

2. **Check stack traces in console**
3. **Review test/surefire-reports** for detailed output
4. **Use IDE debugger** for step-through debugging

---

## Performance Considerations

### Test Execution Time

| Operation | Time | Impact |
|-----------|------|--------|
| Unit Test Setup | ~100ms | Minimal |
| AI Service Call | 3-5 sec | High |
| File I/O | < 100ms | Minimal |
| Cache Operations | < 10ms | Minimal |

**Total Test Suite:** ~35-45 seconds

### Optimization Tips

1. **Mock external service calls** for speed
2. **Reuse test data** across tests
3. **Clean cache** between tests efficiently
4. **Run tests in parallel** with Maven

---

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| ChatModel not found | Check pom.xml dependencies, verify API key |
| File not found | Ensure src/test/resources/ exists with test files |
| Timeout | Increase JVM timeout, check network |
| Memory errors | Increase heap size, clear cache in @BeforeEach |

---

**See 01_QUICK_TEST.md for quick commands**
