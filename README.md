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


This Spring Batch application automatically processes JSON feedback files with chunked processing and scheduled monitoring. The system processes files in chunks of 100 records and continuously monitors for new files every 60 seconds.

### Components

### 1. Batch Job Configuration (`FeedbackBatchJobConfig.java`)
- Contains commented-out static job configuration
- Dynamic job creation is handled in `MultiFileBatchRunner`

### 2. Core Processing Components

#### Item Reader (`FeedbackJsonItemReader.java`)
```java
public class FeedbackJsonItemReader implements ItemReader<FeedbackDTO>
```
- Reads JSON files containing arrays of `FeedbackDTO` objects
- Uses Jackson `ObjectMapper` to parse JSON into Java objects
- Implements iterator pattern for sequential reading

#### Item Processor (`FeedbackItemProcessor.java`)
```java
public class FeedbackItemProcessor implements ItemProcessor<FeedbackDTO, FeedbackDTO>
```
- Currently passes items through unchanged
- Placeholder for validation, normalization, or transformation logic

#### Item Writer (`FeedbackItemWriter.java`)
```java
public class FeedbackItemWriter implements ItemWriter<FeedbackDTO>
```
- Processes chunks of feedback data
- Converts Spring Batch `Chunk` to `List<FeedbackDTO>`
- Delegates to `FeedbackServiceImpl` for database insertion

### 3. Scheduled File Processor (`MultiFileBatchRunner.java`)

#### Key Features:
- **Scheduled Execution**: Runs every 60 seconds using `@Scheduled(fixedDelay = 60000)`
- **Concurrent Protection**: Uses `AtomicBoolean` to prevent overlapping executions
- **File Tracking**: Maintains processing status in database via `FileRecord` entity
- **Dynamic Job Creation**: Creates unique jobs for each file processing

#### Processing Flow:
1. Scans upload directory for `.json` files
2. Checks database to see if file was already processed
3. Creates dynamic Spring Batch job for unprocessed files
4. Executes job with chunk size of 100
5. Marks file as processed and deletes it upon successful completion

## Chunk Processing Details

### Chunk Size Configuration
```java
.<FeedbackDTO, FeedbackDTO>chunk(100, transactionManager)
```

The system processes feedback records in chunks of **100 items**:
- **Read**: Reads up to 100 `FeedbackDTO` objects from JSON
- **Process**: Validates/transforms each item (currently pass-through)
- **Write**: Batch inserts all 100 items to database in single transaction

### Benefits of Chunking:
- **Memory Efficiency**: Prevents loading entire large files into memory
- **Transaction Management**: Each chunk is processed in separate transaction
- **Error Isolation**: Failure in one chunk doesn't affect others
- **Performance**: Optimized batch database operations

### Scheduled Monitoring
```java
@Scheduled(fixedDelay = 60000) // Every 60 seconds
```

The system continuously monitors for new files:
- **Frequency**: Every 60 seconds after previous execution completes
- **Directory**: Configured via `${file.upload-dir}` property
- **File Pattern**: Only processes files ending with `.json`

### Failure Recovery
- **Duplicate Prevention**: Database tracking prevents reprocessing
- **Atomic Operations**: Files only deleted after successful processing
- **Error Logging**: Failed processing attempts are logged with stack traces
- **Retry Logic**: Failed files remain in directory for next scheduled run

### Concurrent Execution Protection
```java
private final AtomicBoolean isRunning = new AtomicBoolean(false);

if (!isRunning.compareAndSet(false, true)) {
    System.out.println("⏳ Batch already running. Skipping this schedule.");
    return;
}
```

## Database Configuration

### Batch Metadata (`BatchConfig.java`)
```java
@Bean
public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager)
```
- Configures Spring Batch metadata repository
- Uses PostgreSQL database
- Stores job execution history and status

### File Tracking
The system uses `FileRecord` entity to track:
- `filename`: Name of processed file
- `processed`: Boolean flag indicating completion status

## Usage Example

### 1. File Structure
```json
[
  {
   ...
  },
  {
   ....
  }
]
```

### 2. Processing Flow
1. Place JSON file in configured upload directory
2. Scheduler detects file within 60 seconds
3. System creates job: `job-{UUID}`
4. Creates step: `step-{UUID}` with chunk size 100
5. Processes file in chunks of 100 records
6. Inserts feedback data via `FeedbackServiceImpl`
7. Marks file as processed and deletes it

### 3. Configuration Properties
```properties
file.upload-dir=/path/to/upload/directory
spring.batch.jdbc.initialize-schema=always
```

## Monitoring & Logs

### Success Indicators
- `✅ Completed and deleted: filename.json`
- `✅ No files to process.`

### Skip Indicators  
- `⏩ Already processed: filename.json`
- `⏳ Batch already running. Skipping this schedule.`

### Error Indicators
- `❌ Failed to process: filename.json`
- `❌ Error with file: filename.json`

## Benefits

1. **Automatic Processing**: No manual intervention required
2. **Fault Tolerance**: Handles failures gracefully with retry capability
3. **Memory Efficient**: Chunk-based processing for large files
4. **Duplicate Prevention**: Database tracking prevents reprocessing
5. **Scalable**: Can handle multiple files and large datasets
6. **Transactional**: Each chunk processed in separate transaction
7. **Monitoring**: Clear logging and status tracking

This system provides a robust, automated solution for processing feedback JSON files with built-in error handling, monitoring, and recovery capabilities.

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

