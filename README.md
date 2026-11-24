# Campus Placement & Eligibility Management System (PlacementHub)

An object-oriented Java application designed for university Training & Placement (T&P) cells to automate student eligibility evaluation, manage corporate recruiter criteria, schedule clash-free recruitment interview rounds, and generate audit reports and synchronized visual web dashboards.

---

## 📌 Key Architectural Features

1. **Rule Evaluation Engine (`EligibilityChecker.java`)**:
   - Multi-metric candidate evaluation checking academic benchmarks: CGPA threshold, backlog limit, and allowed degree disciplines.
   - Categorized evaluation verdicts: `ELIGIBLE`, `CGPA_DEFICIT`, `BACKLOG_VIOLATION`, and `BRANCH_MISMATCH`.
   - Automated candidate ranking by academic standing with backlog tie-breaking.

2. **Indexed Registry Pattern (`StudentManager.java`, `CompanyManager.java`)**:
   - `LinkedHashMap<String, T>` indexing for fast ID lookups while maintaining insertion order.
   - Input validation pipeline checking email formats, CGPA ranges (0.0 – 10.0), and non-negative backlog metrics.
   - Custom exception management avoiding duplicate registrations.

3. **Greedy Clash-Free Interview Scheduler (`InterviewScheduler.java`)**:
   - Automated room and time-slot allocator avoiding concurrent candidate bookings and venue collisions.
   - Prioritizes higher academic merit candidates during slot allocation.
   - Timetable persistence in `interviews.txt`.

4. **Multi-Channel Notification Dispatcher (`NotificationService.java`, `send_real_email.py`)**:
   - Formats placement call letters and status notices with dispatch timestamps.
   - Dispatches email alerts locally to `email_notifications.txt` and connects to a Python SMTP worker for real Gmail delivery.

5. **Live Web Dashboard Synchronization (`ReportGenerator.java`, `dashboard.html`)**:
   - The Java core dynamically synchronizes live student and recruiter records into `dashboard.html`.
   - Allows placement officers to view analytics and candidate grids in a browser without requiring external web frameworks.

---

## 🏗️ Project Architecture & File Hierarchy

```
Student-placement-eligibility-checker/
│
├── Main.java                        # Interactive CLI application controller & menu loop
├── Student.java                     # Student model (ID, Name, CGPA, Branch, Backlogs)
├── Company.java                     # Recruiter model (ID, Name, Cutoffs, Allowed Branches)
├── StudentManager.java              # Indexed registry & file persistence for students
├── CompanyManager.java              # Indexed registry & file persistence for recruiters
├── EligibilityChecker.java          # Evaluation engine & merit leaderboard
├── InterviewScheduler.java          # Clash-free venue and timetable scheduler
├── NotificationService.java         # Placement call letter formatter & dispatcher
├── ReportGenerator.java             # CSV/TXT report exporter & HTML dashboard synchronizer
│
├── PlacementException.java          # Base checked exception
├── DuplicateRecordException.java    # Raised on primary key collisions
├── RecordNotFoundException.java     # Raised on missing record queries
├── ValidationException.java         # Raised on metric out-of-bound errors
├── ScheduleConflictException.java   # Raised on timetable slot exhaustion
│
├── send_real_email.py               # Lightweight Python SMTP dispatch worker
├── email_config.example.json        # SMTP configuration template
├── dashboard.html                   # Interactive browser dashboard (auto-synced)
│
├── students.txt                     # Flat-file database for student records
├── companies.txt                    # Flat-file database for company criteria
├── interviews.txt                   # Flat-file database for booked interview slots
└── email_notifications.txt          # Dispatch logs for sent call letters
```

---

## 🚀 How to Compile and Run

### Prerequisites
* Java JDK 11 or higher (tested on OpenJDK / Oracle JDK 24)
* Python 3.8+ (optional, only needed for live SMTP inbox delivery)

### 1. Compile Java Source Files
Open a terminal in the project directory:
```bash
javac *.java
```

### 2. Run the Application
```bash
java Main
```

Default Admin Credentials:
* **Username**: `admin`
* **Password**: `admin123`

---

## 📊 Design Patterns & Concepts Used

* **Object-Oriented Programming (OOP)**: Encapsulation, abstraction, inheritance (custom exception hierarchy), and polymorphism.
* **Separation of Concerns**: Data models (`Student`, `Company`), business logic (`EligibilityChecker`, `InterviewScheduler`), storage/managers (`StudentManager`, `CompanyManager`), and presentation (`Main`, `dashboard.html`).
* **Flat-File Database Persistence**: Comma-separated values (CSV style) parsed using `BufferedReader` and `BufferedWriter`.
* **Defensive Programming**: Structured `try-catch` blocks and typed custom exceptions preventing crashes on corrupt input data.
