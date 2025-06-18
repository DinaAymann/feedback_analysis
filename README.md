# Feedback Analysis System

## Overview

This backend project implements a scalable feedback ingestion and analytics pipeline using **Spring Boot**, **Spring Batch**, and **PostgreSQL**. The system focuses on high-throughput file ingestion, robust error handling, and a star schema database model to support business intelligence and reporting.

---

## System Architecture

### 1. REST API Layer

#### `FileUploadController`
- **POST `/api/files/upload`**  
  Receives JSON feedback files, checks for duplicates, stores them in `./uploads`, and records metadata (`FileRecord` entity).
- **GET `/api/files/status`**  
  Returns counts/statistics on uploaded, processed, and pending files, including processing rates.

### 2. File Handling Layer

#### `FileStorageService`
- Ensures the `./uploads` directory exists.
- Handles atomic saving of files.
- Checks for file duplicates by filename.

### 3. Batch Processing Layer

#### `BatchConfig` (Spring Batch Configuration)
- **Job:**  
  `importFeedbackJob` is launched per uploaded file.
- **Step:**  
  `importFeedbackStep` reads, validates, and writes feedback in 100-record chunks.
- **ItemReader:**  
  Reads and parses JSON arrays from uploaded files.
- **ItemProcessor:**  
  Validates each record (rating, user, agency, location), performs dimension key lookups, and builds analytics-ready fact records.
- **ItemWriter:**  
  Batch-persist valid feedback records to the database.
- **Error Handling:**  
  Custom skip policy logs and skips validation and parsing errors, tolerating up to 1000 skips per run.

### 4. Database Layer

#### Schema Design (Star Schema)

- **Fact Table:**
  - `Feedback`: Stores individual feedback events, referencing users, agencies, locations, and languages by surrogate keys.
- **Dimension Tables:**
  - `User`: Unique users providing feedback.
  - `Agency`: Agencies/organizations being reviewed.
  - `Location`: Geographical locations tied to feedback.
  - `Language`: Language of the feedback.

#### Example Entity Documentation

##### `Feedback.java`
```java
@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign keys to dimensions
    private Long userKey;
    private Long agencyKey;
    private Long locationKey;
    private Long languageKey;

    // Feedback attributes
    private Integer rating;
    private String comment;
    private LocalDateTime feedbackTimestamp;
    private String processingBatchId;
    // ... other fields and JPA annotations
}
```
- **Purpose:** Fact entity at the center of the star schema. Stores analytic events; keys reference dimension tables for slicing and aggregation.

##### `FileRecord.java`
```java
@Entity
@Table(name = "file_records")
public class FileRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String filename;
    @Column(nullable = false)
    private boolean processed = false;
    private Timestamp uploadedAt;
    private Timestamp processingStartedAt;
    private Timestamp processingCompletedAt;
    private Long recordsProcessed = 0L;
    private Long recordsFailed = 0L;
    private String errorMessage;
}
```
- **Purpose:** Tracks all uploaded files and their batch processing status. Guarantees idempotency and reliable job management.

##### `JobExecutionStatus.java`
- Stores metadata and execution status per batch job for monitoring and auditing purposes.

---

## Key Code Modules

- **`FileUploadController`**:  
  Handles file ingestion, prevents duplicates, and triggers batch jobs on upload.

- **`FileStorageService`**:  
  Encapsulates file IO, ensures safe storage, prevents duplicates.

- **`BatchConfig`**:  
  - Sets up Spring Batch jobs and steps.
  - Defines chunk size (100), error skip policy, processor logic, and listeners for job/step lifecycle events.

- **`FeedbackRepository`, `FileRecordRepository`, `JobExecutionStatusRepository`**:  
  JPA repositories for fact, control/meta, and job status data.

---

## Design Highlights

- **Chunk-Oriented Batch Processing:**  
  100 records per chunk for memory efficiency.
- **Resilience:**  
  Tolerates bad records, logs all issues, and continues processing.
- **Star Schema:**  
  - Fact table (`Feedback`) at the core.
  - Dimensions (`User`, `Agency`, `Location`, `Language`) referenced by surrogate keys.
  - Supports fast aggregations and filtering.
- **API Simplicity:**  
  Clean separation of upload, processing, and status endpoints.

---

## How to Use

1. **Start the backend (Java 17+, PostgreSQL):**
   ```
   mvn clean package
   java -jar target/feedback-*.jar
   ```

2. **Upload a file:**
   ```
   curl -F "file=@feedback.json" http://localhost:8080/api/files/upload
   ```

3. **Check processing status:**
   ```
   curl http://localhost:8080/api/files/status
   ```

---

## Entity Relationships (Star Schema Example)

```plaintext
          +-----------+     +---------+     +-----------+
          |  User     |     | Agency  |     | Location  |
          +-----------+     +---------+     +-----------+
                \               |               /
                 \              |              /
                  \             |             /
                   +-------------------------+
                   |       Feedback          |
                   +-------------------------+
                            |
                        +--------+
                        |Language|
                        +--------+
```

---

## Why This Approach

- **Spring Batch** enables scalable, fault-tolerant ingestion.
- **Star schema** supports business analytics and BI tools (like Power BI) for fast, flexible reporting.
- **Idempotent APIs** and batch jobs ensure data integrity and operational reliability.

---

