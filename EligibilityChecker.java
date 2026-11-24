import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EligibilityChecker {

    public enum EligibilityVerdict {
        ELIGIBLE("Candidate satisfies all qualification criteria."),
        CGPA_DEFICIT("CGPA is below the minimum recruitment threshold."),
        BACKLOG_VIOLATION("Active backlog count exceeds company tolerance."),
        BRANCH_MISMATCH("Degree discipline/branch is not among the designated hiring branches.");

        private final String description;

        EligibilityVerdict(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class EvaluationResult {
        private final boolean eligible;
        private final EligibilityVerdict primaryVerdict;
        private final boolean cgpaPassed;
        private final boolean backlogPassed;
        private final boolean branchPassed;
        private final double cgpaDelta;

        public EvaluationResult(boolean eligible, EligibilityVerdict verdict,
                                boolean cgpaPassed, boolean backlogPassed, boolean branchPassed, double cgpaDelta) {
            this.eligible = eligible;
            this.primaryVerdict = verdict;
            this.cgpaPassed = cgpaPassed;
            this.backlogPassed = backlogPassed;
            this.branchPassed = branchPassed;
            this.cgpaDelta = cgpaDelta;
        }

        public boolean isEligible() { return eligible; }
        public EligibilityVerdict getVerdict() { return primaryVerdict; }
        public boolean isCgpaPassed() { return cgpaPassed; }
        public boolean isBacklogPassed() { return backlogPassed; }
        public boolean isBranchPassed() { return branchPassed; }
        public double getCgpaDelta() { return cgpaDelta; }
    }

    public static EvaluationResult evaluate(Student student, Company company) {
        if (student == null || company == null) {
            return new EvaluationResult(false, EligibilityVerdict.CGPA_DEFICIT, false, false, false, 0.0);
        }

        boolean cgpaOk = student.getCgpa() >= company.getMinimumCgpa();
        boolean backlogsOk = student.getBacklogs() <= company.getMaximumBacklogs();
        double cgpaGap = student.getCgpa() - company.getMinimumCgpa();

        ArrayList<String> allowedBranches = company.getEligibleBranches();
        boolean branchOk = true;
        if (allowedBranches != null && !allowedBranches.isEmpty()) {
            branchOk = false;
            for (String allowed : allowedBranches) {
                if (allowed.trim().equalsIgnoreCase(student.getBranch().trim())) {
                    branchOk = true;
                    break;
                }
            }
        }

        EligibilityVerdict verdict = EligibilityVerdict.ELIGIBLE;
        if (!cgpaOk) {
            verdict = EligibilityVerdict.CGPA_DEFICIT;
        } else if (!backlogsOk) {
            verdict = EligibilityVerdict.BACKLOG_VIOLATION;
        } else if (!branchOk) {
            verdict = EligibilityVerdict.BRANCH_MISMATCH;
        }

        boolean overall = cgpaOk && backlogsOk && branchOk;
        return new EvaluationResult(overall, verdict, cgpaOk, backlogsOk, branchOk, cgpaGap);
    }

    public static boolean isEligible(Student student, Company company) {
        return evaluate(student, company).isEligible();
    }

    public static void printEligibilityReport(Student student, Company company) {
        EvaluationResult res = evaluate(student, company);

        System.out.println("\n========== ELIGIBILITY EVALUATION ==========");
        System.out.println("Candidate : " + student.getName() + " [ID: " + student.getStudentId() + "]");
        System.out.println("Recruiter : " + company.getCompanyName() + " [ID: " + company.getCompanyId() + "]");
        System.out.println("--------------------------------------------");

        System.out.printf("1. CGPA Metric    : %-5.2f | Cutoff: %-5.2f | %s (Diff: %+.2f)%n",
                student.getCgpa(), company.getMinimumCgpa(),
                res.isCgpaPassed() ? "PASS" : "FAIL", res.getCgpaDelta());

        System.out.printf("2. Backlog Count  : %-5d | Max Limit: %-2d | %s%n",
                student.getBacklogs(), company.getMaximumBacklogs(),
                res.isBacklogPassed() ? "PASS" : "FAIL");

        ArrayList<String> branches = company.getEligibleBranches();
        String allowedDesc = (branches == null || branches.isEmpty()) ? "All Disciplines" : branches.toString();
        System.out.printf("3. Academic Branch: %-5s | Allowed: %s | %s%n",
                student.getBranch(), allowedDesc,
                res.isBranchPassed() ? "PASS" : "FAIL");

        System.out.println("--------------------------------------------");
        if (res.isEligible()) {
            System.out.println("FINAL STATUS : [ELIGIBLE] Candidate cleared all criteria.");
        } else {
            System.out.println("FINAL STATUS : [NOT ELIGIBLE] Reason: " + res.getVerdict().getDescription());
        }
        System.out.println("============================================");
    }

    public static void printEligibleStudentsForCompany(Company company, ArrayList<Student> studentList) {
        System.out.println("\n========== SHORTLIST: " + company.getCompanyName().toUpperCase() + " ==========");
        System.out.printf("Criteria -> Min CGPA: %.2f | Max Backlogs: %d%n",
                company.getMinimumCgpa(), company.getMaximumBacklogs());
        System.out.println("--------------------------------------------------");

        ArrayList<Student> shortlisted = new ArrayList<>();
        for (Student s : studentList) {
            if (isEligible(s, company)) {
                shortlisted.add(s);
            }
        }

        if (shortlisted.isEmpty()) {
            System.out.println("No matching candidate pool found for this recruiter.");
        } else {
            // Sort by CGPA descending for convenient review
            shortlisted.sort((a, b) -> Double.compare(b.getCgpa(), a.getCgpa()));

            for (Student s : shortlisted) {
                System.out.printf("%-10s %-20s %-8s CGPA: %-5.2f Backlogs: %d%n",
                        s.getStudentId(), s.getName(), s.getBranch(), s.getCgpa(), s.getBacklogs());
            }
            System.out.println("--------------------------------------------------");
            System.out.println("Total Shortlisted Candidates: " + shortlisted.size());
        }
    }

    public static void rankStudents(ArrayList<Student> studentList) {
        if (studentList == null || studentList.isEmpty()) {
            System.out.println("Notice: No student records present to generate leaderboard.");
            return;
        }

        ArrayList<Student> leaderboard = new ArrayList<>(studentList);

        leaderboard.sort((s1, s2) -> {
            int cmp = Double.compare(s2.getCgpa(), s1.getCgpa());
            if (cmp != 0) return cmp;
            return Integer.compare(s1.getBacklogs(), s2.getBacklogs());
        });

        System.out.println("\n========== ACADEMIC MERIT LEADERBOARD ==========");
        System.out.printf("%-6s %-10s %-20s %-8s %-6s %-8s%n",
                "Rank", "ID", "Name", "Branch", "CGPA", "Backlogs");
        System.out.println("--------------------------------------------------");
        int pos = 1;
        for (Student s : leaderboard) {
            System.out.printf("%-6d %-10s %-20s %-8s %-6.2f %-8d%n",
                    pos++, s.getStudentId(), s.getName(), s.getBranch(), s.getCgpa(), s.getBacklogs());
        }
        System.out.println("================================================");
    }
}
