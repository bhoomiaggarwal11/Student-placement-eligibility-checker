import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static StudentManager studentManager = new StudentManager();
    static CompanyManager companyManager = new CompanyManager();
    static InterviewScheduler scheduler = new InterviewScheduler();

    static boolean isAdminLoggedIn = false;
    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        System.out.println("Loaded " + studentManager.getStudentCount() + " students and "
                + companyManager.getCompanyCount() + " companies.");

        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    handleAdminLogin();
                    break;
                case 2:
                    handleStudentManagement();
                    break;
                case 3:
                    handleCompanyManagement();
                    break;
                case 4:
                    handleCheckEligibility();
                    break;
                case 5:
                    handleEligibleStudentLists();
                    break;
                case 6:
                    handleRankStudents();
                    break;
                case 7:
                    handleExportReports();
                    break;
                case 8:
                    handleInterviewScheduler();
                    break;
                case 9:
                    handleNotificationService();
                    break;
                case 10:
                    System.out.println("Data saved. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 10.");
            }

            if (running) {
                System.out.print("\nPress ENTER to return to main menu...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    static void displayMainMenu() {
        String loginStatus = isAdminLoggedIn ? "[Admin Logged In]" : "[Guest Mode]";
        System.out.println("\n==========================================");
        System.out.println("   MAIN MENU          " + loginStatus);
        System.out.println("==========================================");
        System.out.println("1. Admin Login / Logout");
        System.out.println("2. Student Management");
        System.out.println("3. Company Management");
        System.out.println("4. Check Student Eligibility");
        System.out.println("5. View Eligible Students for a Company");
        System.out.println("6. Rank Students by CGPA");
        System.out.println("7. Export Placement Reports");
        System.out.println("8. Automated Interview & Room Scheduler");
        System.out.println("9. Placement Email & Notification Dispatcher");
        System.out.println("10. Exit");
        System.out.println("==========================================");
    }

    static void handleAdminLogin() {
        if (isAdminLoggedIn) {
            System.out.println("You are already logged in.");
            String opt = getStringInput("Do you want to logout? (yes/no): ");
            if (opt.equalsIgnoreCase("yes")) {
                isAdminLoggedIn = false;
                System.out.println("Logged out successfully.");
            }
            return;
        }
        System.out.println("\n--- Admin Login ---");
        String username = getStringInput("Username: ");
        String password = getStringInput("Password: ");

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            isAdminLoggedIn = true;
            System.out.println("Login successful. Welcome, Admin!");
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    static void handleStudentManagement() {
        System.out.println("\n--- Student Management ---");
        System.out.println("1. Add New Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student by ID");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Back");

        int choice = getIntInput("Select option: ");

        switch (choice) {
            case 1:
                if (!checkAdmin()) return;
                addStudent();
                break;
            case 2:
                studentManager.displayAllStudents();
                break;
            case 3:
                searchStudent();
                break;
            case 4:
                if (!checkAdmin()) return;
                updateStudent();
                break;
            case 5:
                if (!checkAdmin()) return;
                deleteStudent();
                break;
            case 6:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    static void addStudent() {
        System.out.println("\n--- Add New Student ---");
        String id = getStringInput("Student ID (e.g. STU101): ");
        String name = getStringInput("Full Name: ");
        String email = getStringInput("Email: ");
        String phone = getStringInput("Phone: ");
        String branch = getStringInput("Branch (CSE/IT/ECE/MECH/CIVIL): ").toUpperCase();
        int semester = getIntInput("Semester (1-8): ");
        double cgpa = getDoubleInput("CGPA (0.0 - 10.0): ");
        int backlogs = getIntInput("Number of Backlogs: ");

        Student student = new Student(id, name, email, phone, branch, semester, cgpa, backlogs);
        studentManager.addStudent(student);
    }

    static void searchStudent() {
        String id = getStringInput("Enter Student ID: ");
        Student student = studentManager.findStudentById(id);
        if (student != null) {
            student.display();
        } else {
            System.out.println("Student not found with ID: " + id);
        }
    }

    static void updateStudent() {
        String id = getStringInput("Enter Student ID to update: ");
        Student existing = studentManager.findStudentById(id);
        if (existing == null) {
            System.out.println("Student not found with ID: " + id);
            return;
        }
        System.out.println("Updating: " + existing.getName());
        String name = getStringInput("New Name: ");
        String email = getStringInput("New Email: ");
        String phone = getStringInput("New Phone: ");
        String branch = getStringInput("New Branch: ").toUpperCase();
        int semester = getIntInput("New Semester: ");
        double cgpa = getDoubleInput("New CGPA: ");
        int backlogs = getIntInput("New Backlogs: ");

        studentManager.updateStudent(id, name, email, phone, branch, semester, cgpa, backlogs);
    }

    static void deleteStudent() {
        String id = getStringInput("Enter Student ID to delete: ");
        studentManager.deleteStudent(id);
    }

    static void handleCompanyManagement() {
        System.out.println("\n--- Company Management ---");
        System.out.println("1. Add New Company");
        System.out.println("2. View All Companies");
        System.out.println("3. Search Company by ID");
        System.out.println("4. Search Company by Name");
        System.out.println("5. Update Company");
        System.out.println("6. Delete Company");
        System.out.println("7. Back");

        int choice = getIntInput("Select option: ");

        switch (choice) {
            case 1:
                if (!checkAdmin()) return;
                addCompany();
                break;
            case 2:
                companyManager.displayAllCompanies();
                break;
            case 3:
                searchCompanyById();
                break;
            case 4:
                searchCompanyByName();
                break;
            case 5:
                if (!checkAdmin()) return;
                updateCompany();
                break;
            case 6:
                if (!checkAdmin()) return;
                deleteCompany();
                break;
            case 7:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    static void addCompany() {
        System.out.println("\n--- Add New Company ---");
        String id = getStringInput("Company ID (e.g. CMP101): ");
        String name = getStringInput("Company Name: ");
        double minCgpa = getDoubleInput("Minimum CGPA required: ");
        int maxBacklogs = getIntInput("Maximum Backlogs allowed: ");
        String branchInput = getStringInput("Eligible Branches (comma-separated, e.g. CSE,IT,ECE or leave blank for ALL): ");

        ArrayList<String> branches = new ArrayList<>();
        if (!branchInput.trim().isEmpty()) {
            String[] parts = branchInput.split(",");
            for (String b : parts) {
                if (!b.trim().isEmpty()) {
                    branches.add(b.trim().toUpperCase());
                }
            }
        }

        Company company = new Company(id, name, minCgpa, maxBacklogs, branches);
        companyManager.addCompany(company);
    }

    static void searchCompanyById() {
        String id = getStringInput("Enter Company ID: ");
        Company company = companyManager.findCompanyById(id);
        if (company != null) {
            company.display();
        } else {
            System.out.println("Company not found with ID: " + id);
        }
    }

    static void searchCompanyByName() {
        String name = getStringInput("Enter Company Name (or part of it): ");
        Company company = companyManager.findCompanyByName(name);
        if (company != null) {
            company.display();
        } else {
            System.out.println("No company found matching: " + name);
        }
    }

    static void updateCompany() {
        String id = getStringInput("Enter Company ID to update: ");
        Company existing = companyManager.findCompanyById(id);
        if (existing == null) {
            System.out.println("Company not found with ID: " + id);
            return;
        }
        System.out.println("Updating: " + existing.getCompanyName());
        String name = getStringInput("New Name: ");
        double minCgpa = getDoubleInput("New Minimum CGPA: ");
        int maxBacklogs = getIntInput("New Maximum Backlogs: ");
        String branchInput = getStringInput("New Eligible Branches (comma-separated or blank for ALL): ");

        ArrayList<String> branches = new ArrayList<>();
        if (!branchInput.trim().isEmpty()) {
            String[] parts = branchInput.split(",");
            for (String b : parts) {
                if (!b.trim().isEmpty()) {
                    branches.add(b.trim().toUpperCase());
                }
            }
        }

        companyManager.updateCompany(id, name, minCgpa, maxBacklogs, branches);
    }

    static void deleteCompany() {
        String id = getStringInput("Enter Company ID to delete: ");
        companyManager.deleteCompany(id);
    }

    static void handleCheckEligibility() {
        System.out.println("\n--- Check Student Eligibility ---");
        String studentId = getStringInput("Enter Student ID: ");
        Student student = studentManager.findStudentById(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        String companyId = getStringInput("Enter Company ID: ");
        Company company = companyManager.findCompanyById(companyId);
        if (company == null) {
            System.out.println("Company not found.");
            return;
        }

        EligibilityChecker.printEligibilityReport(student, company);
    }

    static void handleEligibleStudentLists() {
        System.out.println("\n--- Eligible Students for a Company ---");
        if (companyManager.getAllCompanies().isEmpty()) {
            System.out.println("No companies found.");
            return;
        }

        companyManager.displayAllCompanies();
        String input = getStringInput("Enter Company ID (or type ALL for every company): ");

        if (input.equalsIgnoreCase("ALL")) {
            for (Company c : companyManager.getAllCompanies()) {
                EligibilityChecker.printEligibleStudentsForCompany(c, studentManager.getAllStudents());
            }
        } else {
            Company company = companyManager.findCompanyById(input);
            if (company == null) {
                System.out.println("Company not found.");
                return;
            }
            EligibilityChecker.printEligibleStudentsForCompany(company, studentManager.getAllStudents());
        }
    }

    static void handleRankStudents() {
        System.out.println("\n--- Student Ranking ---");
        if (studentManager.getAllStudents().isEmpty()) {
            System.out.println("No students to rank.");
            return;
        }
        EligibilityChecker.rankStudents(studentManager.getAllStudents());
    }

    static void handleExportReports() {
        System.out.println("\n--- Export Reports ---");
        System.out.println("1. Export Student Report as CSV");
        System.out.println("2. Export Student Report as TXT");
        System.out.println("3. Export Eligible Students Report as CSV");
        System.out.println("4. Export Eligible Students Report as TXT");
        System.out.println("5. Export All Reports (CSV + TXT)");
        System.out.println("6. Back");

        int choice = getIntInput("Select option: ");

        ArrayList<Student> students = studentManager.getAllStudents();
        ArrayList<Company> companies = companyManager.getAllCompanies();

        switch (choice) {
            case 1:
                ReportGenerator.exportStudentReport(students, "csv");
                break;
            case 2:
                ReportGenerator.exportStudentReport(students, "txt");
                break;
            case 3:
                ReportGenerator.exportEligibleStudentsReport(companies, students, "csv");
                break;
            case 4:
                ReportGenerator.exportEligibleStudentsReport(companies, students, "txt");
                break;
            case 5:
                ReportGenerator.exportStudentReport(students, "csv");
                ReportGenerator.exportStudentReport(students, "txt");
                ReportGenerator.exportEligibleStudentsReport(companies, students, "csv");
                ReportGenerator.exportEligibleStudentsReport(companies, students, "txt");
                System.out.println("All reports exported successfully.");
                break;
            case 6:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    static void handleInterviewScheduler() {
        System.out.println("\n--- Automated Interview & Room Scheduler ---");
        System.out.println("1. Auto-Schedule Interviews for Eligible Candidates");
        System.out.println("2. View Current Interview Schedule & Room Allocations");
        System.out.println("3. Clear / Reset All Interview Schedules");
        System.out.println("4. Back");

        int choice = getIntInput("Select option: ");
        switch (choice) {
            case 1:
                if (!checkAdmin()) return;
                scheduleInterviews();
                break;
            case 2:
                scheduler.displayAllSchedules();
                break;
            case 3:
                if (!checkAdmin()) return;
                scheduler.clearAllSchedules();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    static void scheduleInterviews() {
        if (companyManager.getAllCompanies().isEmpty()) {
            System.out.println("No companies registered.");
            return;
        }

        companyManager.displayAllCompanies();
        String companyId = getStringInput("Enter Company ID to schedule interviews for: ");
        Company company = companyManager.findCompanyById(companyId);
        if (company == null) {
            System.out.println("Company not found.");
            return;
        }

        ArrayList<Student> eligible = new ArrayList<>();
        for (Student s : studentManager.getAllStudents()) {
            if (EligibilityChecker.isEligible(s, company)) {
                eligible.add(s);
            }
        }

        if (eligible.isEmpty()) {
            System.out.println("No eligible candidates found for " + company.getCompanyName() + " to schedule.");
            return;
        }

        System.out.println("\nSelect Interview Round:");
        System.out.println("1. Tech Round 1");
        System.out.println("2. System Design");
        System.out.println("3. Behavioral / HR");
        System.out.println("4. Quant Round");
        System.out.println("5. Custom Round");
        int roundChoice = getIntInput("Enter choice (1-5): ");
        String roundName;
        switch (roundChoice) {
            case 1: roundName = "Tech Round 1"; break;
            case 2: roundName = "System Design"; break;
            case 3: roundName = "Behavioral / HR"; break;
            case 4: roundName = "Quant Round"; break;
            default: roundName = getStringInput("Enter custom round name: "); break;
        }

        String date = getStringInput("Enter Drive Date (e.g. 25-Nov-2026): ");
        int scheduled = scheduler.autoSchedule(company, eligible, roundName, date);
        System.out.println("Successfully scheduled " + scheduled + " clash-free interview slots across available rooms!");
        scheduler.displayAllSchedules();
    }

    static void handleNotificationService() {
        System.out.println("\n--- Placement Email & Notification Dispatcher ---");
        System.out.println("1. Broadcast Interview Call Letters (For Scheduled Candidates)");
        System.out.println("2. Send Individual Interview Call Letter");
        System.out.println("3. Send Eligibility Notification (Eligible / Not Eligible Alert)");
        System.out.println("4. Back");

        int choice = getIntInput("Select option: ");
        switch (choice) {
            case 1:
                if (!checkAdmin()) return;
                if (scheduler.getScheduleList().isEmpty()) {
                    System.out.println("No interview slots scheduled yet. Please schedule interviews first (Option 8).");
                    return;
                }
                int count = NotificationService.broadcastSchedules(scheduler.getScheduleList(), studentManager);
                System.out.println("\n>> Successfully broadcasted " + count + " call letters! Check email_notifications.txt for dispatched logs.");
                break;
            case 2:
                if (!checkAdmin()) return;
                sendManualCallLetter();
                break;
            case 3:
                if (!checkAdmin()) return;
                sendManualEligibilityAlert();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    static void sendManualCallLetter() {
        String studentId = getStringInput("Enter Student ID: ");
        Student s = studentManager.findStudentById(studentId);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }

        String companyName = getStringInput("Company Name: ");
        String roundName = getStringInput("Round Name (e.g. Tech Round 1): ");
        String room = getStringInput("Room (e.g. Room A - Suite 1): ");
        String timeSlot = getStringInput("Time Slot (e.g. 10:30 AM - 12:00 PM): ");
        String date = getStringInput("Date (e.g. 25-Nov-2026): ");

        NotificationService.sendInterviewCallLetter(s, companyName, roundName, room, timeSlot, date);
    }

    static void sendManualEligibilityAlert() {
        String studentId = getStringInput("Enter Student ID: ");
        Student s = studentManager.findStudentById(studentId);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }

        String companyId = getStringInput("Enter Company ID: ");
        Company c = companyManager.findCompanyById(companyId);
        if (c == null) {
            System.out.println("Company not found.");
            return;
        }

        boolean eligible = EligibilityChecker.isEligible(s, c);
        String reason = eligible ? "Meets cutoff" : "CGPA / Backlog / Branch requirements not met";
        NotificationService.sendEligibilityAlert(s, c, eligible, reason);
    }

    static boolean checkAdmin() {
        if (!isAdminLoggedIn) {
            System.out.println("Access denied. Please login as Admin first (Option 1).");
            return false;
        }
        return true;
    }

    static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid decimal number (e.g. 7.5).");
            }
        }
    }

    static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
