import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EligibilityChecker {

    public static boolean isEligible(Student student, Company company) {
        if (student.getCgpa() < company.getMinimumCgpa()) {
            return false;
        }
        if (student.getBacklogs() > company.getMaximumBacklogs()) {
            return false;
        }
        ArrayList<String> branches = company.getEligibleBranches();
        if (!branches.isEmpty()) {
            boolean branchMatch = false;
            for (String branch : branches) {
                if (branch.equalsIgnoreCase(student.getBranch())) {
                    branchMatch = true;
                    break;
                }
            }
            if (!branchMatch) {
                return false;
            }
        }
        return true;
    }

    public static void printEligibilityReport(Student student, Company company) {
        System.out.println("\n========== ELIGIBILITY CHECK ==========");
        System.out.println("Student : " + student.getName() + " (" + student.getStudentId() + ")");
        System.out.println("Company : " + company.getCompanyName() + " (" + company.getCompanyId() + ")");
        System.out.println("----------------------------------------");

        boolean cgpaPassed = student.getCgpa() >= company.getMinimumCgpa();
        boolean backlogsPassed = student.getBacklogs() <= company.getMaximumBacklogs();

        ArrayList<String> branches = company.getEligibleBranches();
        boolean branchPassed = true;
        if (!branches.isEmpty()) {
            branchPassed = false;
            for (String branch : branches) {
                if (branch.equalsIgnoreCase(student.getBranch())) {
                    branchPassed = true;
                    break;
                }
            }
        }

        System.out.println("CGPA Check    : Student=" + student.getCgpa()
                + " | Required=" + company.getMinimumCgpa()
                + " | " + (cgpaPassed ? "PASSED" : "FAILED"));

        System.out.println("Backlog Check : Student=" + student.getBacklogs()
                + " | Allowed=" + company.getMaximumBacklogs()
                + " | " + (backlogsPassed ? "PASSED" : "FAILED"));

        System.out.println("Branch Check  : Student=" + student.getBranch()
                + " | Allowed=" + (branches.isEmpty() ? "All" : branches)
                + " | " + (branchPassed ? "PASSED" : "FAILED"));

        System.out.println("----------------------------------------");
        if (cgpaPassed && backlogsPassed && branchPassed) {
            System.out.println("Result: ELIGIBLE");
        } else {
            System.out.println("Result: NOT ELIGIBLE");
        }
        System.out.println("========================================");
    }

    public static void printEligibleStudentsForCompany(Company company, ArrayList<Student> studentList) {
        System.out.println("\n========== ELIGIBLE STUDENTS FOR: " + company.getCompanyName() + " ==========");
        System.out.println("Min CGPA: " + company.getMinimumCgpa()
                + " | Max Backlogs: " + company.getMaximumBacklogs()
                + " | Branches: " + (company.getEligibleBranches().isEmpty() ? "All" : company.getEligibleBranches()));
        System.out.println("--------------------------------------------------");

        ArrayList<Student> eligibleStudents = new ArrayList<>();
        for (Student s : studentList) {
            if (isEligible(s, company)) {
                eligibleStudents.add(s);
            }
        }

        if (eligibleStudents.isEmpty()) {
            System.out.println("No eligible students found for this company.");
        } else {
            for (Student s : eligibleStudents) {
                System.out.printf("%-10s %-20s %-8s CGPA: %-5.2f Backlogs: %d%n",
                        s.getStudentId(), s.getName(), s.getBranch(), s.getCgpa(), s.getBacklogs());
            }
            System.out.println("--------------------------------------------------");
            System.out.println("Total Eligible: " + eligibleStudents.size());
        }
    }

    public static void rankStudents(ArrayList<Student> studentList) {
        if (studentList.isEmpty()) {
            System.out.println("No students to rank.");
            return;
        }

        ArrayList<Student> rankedList = new ArrayList<>(studentList);

        Collections.sort(rankedList, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                int cgpaCompare = Double.compare(s2.getCgpa(), s1.getCgpa());
                if (cgpaCompare != 0) {
                    return cgpaCompare;
                }
                return Integer.compare(s1.getBacklogs(), s2.getBacklogs());
            }
        });

        System.out.println("\n========== STUDENT RANKING (By CGPA) ==========");
        System.out.printf("%-6s %-10s %-20s %-8s %-6s %-8s%n",
                "Rank", "ID", "Name", "Branch", "CGPA", "Backlogs");
        System.out.println("--------------------------------------------------");
        int rank = 1;
        for (Student s : rankedList) {
            System.out.printf("%-6d %-10s %-20s %-8s %-6.2f %-8d%n",
                    rank, s.getStudentId(), s.getName(), s.getBranch(), s.getCgpa(), s.getBacklogs());
            rank++;
        }
        System.out.println("==================================================");
    }
}
