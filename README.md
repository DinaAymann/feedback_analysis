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
### Star Schema Design

The system implements a classic star schema with the following structure:

```
         ┌─────────────┐
         │  DimDate    │
         │ (dim_date)  │
         └─────────────┘
                │
                │
    ┌─────────────────────────────────────┐
    │        FactFeedback                 │
    │     (fact_feedback)                 │
    │  - Tweet/Post interactions          │
    │  - Engagement metrics               │
    │  - Content analysis                 │
    └─────────────────────────────────────┘
         │         │         │         │
         │         │         │         │
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│   DimUser   │ │  DimIssue   │ │ DimLocation │ │  DimAgency  │
│ (dim_user)  │ │ (dim_issue) │ │(dim_location)│ │(dim_agency) │
└─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
```

### Entity Descriptions

#### Fact Table
- **`FactFeedback`** (`fact_feedback`)
  - **Primary Key**: `tweetId` (Long)
  - **Metrics**: Engagement data (likes, retweets, replies, quotes, bookmarks, impressions)
  - **Content**: Tweet text, language, platform
  - **Relationships**: Foreign keys to all dimension tables
  - **Computed Fields**: `totalInteraction` (transient field calculated from engagement metrics)

#### Dimension Tables

1. **`DimDate`** (`dim_date`)
   - **Primary Key**: `dateKey` (Integer, YYYYMMDD format)
   - **Attributes**: Full date, year, month, day, week, weekday name
   - **Purpose**: Time-based analysis and reporting

2. **`DimUser`** (`dim_user`)
   - **Primary Key**: `userKey` (Integer, auto-generated)
   - **Unique**: `userId` (String)
   - **Attributes**: Username, account creation date, follower metrics, tweet count
   - **Purpose**: User behavior and influence analysis

3. **`DimAgency`** (`dim_agency`)
   - **Primary Key**: `agencyKey` (Integer, auto-generated)
   - **Unique**: `mention` (String)
   - **Purpose**: Government agency mention tracking
   - **Relationships**: One-to-many with `FactMention`

4. **`DimIssue`** (`dim_issue`)
   - **Primary Key**: `issueKey` (Integer, auto-generated)
   - **Attributes**: Issue classification and categorization
   - **Purpose**: Topic and issue-based analysis

5. **`DimLocation`** (`dim_location`)
   - **Primary Key**: `locationKey` (Integer, auto-generated)
   - **Attributes**: Location string, city, country
   - **Purpose**: Geographic analysis of feedback

6. **`DimHashtag`** (`dim_hashtag`)
   - **Primary Key**: `hashtagId` (Long, auto-generated)
   - **Relationships**: Many-to-one with `FactFeedback`
   - **Purpose**: Hashtag trend analysis

#### Bridge Tables

- **`FactMention`** (`fact_mentions`)
  - **Composite Primary Key**: `FactMentionId` (tweet + agency)
  - **Purpose**: Many-to-many relationship between tweets and agency mentions
  - **Design**: Supports multiple agency mentions per tweet

## 🚀 Key Features

### 1. RESTful API Endpoints
The system provides two main RESTful endpoints for:
- **Data Retrieval**: Query feedback data with various filters
- **File Management**: Handle file uploads and processing status

### 2. Automated Data Processing
- **Spring Batch Integration**: Automated processing of uploaded files
- **Continuous Monitoring**: System checks for new files every minute
- **Fault Tolerance**: Automatic retry mechanism for failed processing jobs
- **Status Tracking**: Complete job execution monitoring through `JobExecutionStatus`

### 3. File Processing Pipeline
- **File Upload Detection**: Monitors for new data files
- **Batch Processing**: Processes files using Spring Batch framework
- **Status Management**: Tracks processing status via `FileRecord` entity
- **Error Handling**: Comprehensive error logging and recovery

## 🛠️ Technical Implementation

### Technologies Used
- **Framework**: Spring Boot 3+ (Jakarta EE)
- **ORM**: JPA/Hibernate
- **Database**: Relational database (MySQL/PostgreSQL compatible)
- **Batch Processing**: Spring Batch
- **Data Binding**: Lombok for reduced boilerplate
- **Architecture**: Microservices-ready design

### Key Design Patterns
1. **Star Schema**: Optimized for analytical queries
2. **Composite Keys**: Complex relationships with `@IdClass`
3. **Cascade Operations**: Automatic entity lifecycle management
4. **Lazy Loading**: Optimized data fetching strategies


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

## 📋 What Has Been Completed

### ✅ Core Data Model
- [x] Complete star schema implementation
- [x] All dimension tables with proper relationships
- [x] Fact table with comprehensive metrics
- [x] Bridge tables for many-to-many relationships
- [x] Composite key implementations

### ✅ Entity Relationships
- [x] JPA annotations and mappings
- [x] Bidirectional relationships with proper cascade settings
- [x] Orphan removal for data integrity
- [x] Foreign key constraints

### ✅ Batch Processing System
- [x] Spring Batch configuration
- [x] File monitoring and detection
- [x] Automated processing pipeline
- [x] Job execution status tracking
- [x] Error handling and logging

### ✅ API Infrastructure
- [x] RESTful endpoint structure
- [x] Data access layer foundations
- [x] File management capabilities

### ✅ Data Integrity Features
- [x] Unique constraints on critical fields
- [x] Proper data types for all fields
- [x] Transient fields for computed values
- [x] Comprehensive error tracking

