import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReportGenerator {

    private static final String REPORTS_DIR = "reports";
    private static final String DASHBOARD_FILE = "dashboard.html";

    private static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    private static void ensureOutputDirectory() {
        File dir = new File(REPORTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void exportStudentReport(ArrayList<Student> studentList, String format) {
        ensureOutputDirectory();
        String extension = format != null && format.equalsIgnoreCase("csv") ? "csv" : "txt";
        String targetPath = REPORTS_DIR + "/students_audit_" + getTimestamp() + "." + extension;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath))) {
            if ("csv".equalsIgnoreCase(extension)) {
                bw.write("StudentID,FullName,Email,Phone,Discipline,Semester,CGPA,ActiveBacklogs");
                bw.newLine();
                for (Student s : studentList) {
                    bw.write(String.format("%s,%s,%s,%s,%s,%d,%.2f,%d",
                            s.getStudentId(), s.getName(), s.getEmail(), s.getPhone(),
                            s.getBranch(), s.getSemester(), s.getCgpa(), s.getBacklogs()));
                    bw.newLine();
                }
            } else {
                bw.write("========================================================================\n");
                bw.write("                STUDENT ACADEMIC & PLACEMENT AUDIT REPORT               \n");
                bw.write("Generated On: " + new Date() + "\n");
                bw.write("========================================================================\n\n");
                for (Student s : studentList) {
                    bw.write(String.format("ID: %-8s | Name: %-20s | Branch: %-6s | CGPA: %-4.2f | Backlogs: %d\n",
                            s.getStudentId(), s.getName(), s.getBranch(), s.getCgpa(), s.getBacklogs()));
                }
                bw.write("\n------------------------------------------------------------------------\n");
                bw.write("Total Registered Cohort: " + studentList.size() + "\n");
            }
            System.out.println("Audit Report generated: " + targetPath);
        } catch (IOException e) {
            System.out.println("Failed to write student report: " + e.getMessage());
        }
    }

    public static void exportEligibleStudentsReport(ArrayList<Company> companyList,
                                                    ArrayList<Student> studentList, String format) {
        ensureOutputDirectory();
        String extension = format != null && format.equalsIgnoreCase("csv") ? "csv" : "txt";
        String targetPath = REPORTS_DIR + "/company_eligibility_matrix_" + getTimestamp() + "." + extension;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath))) {
            if ("csv".equalsIgnoreCase(extension)) {
                bw.write("CompanyID,CompanyName,StudentID,CandidateName,Branch,CGPA,Backlogs,Status");
                bw.newLine();
                for (Company c : companyList) {
                    for (Student s : studentList) {
                        if (EligibilityChecker.isEligible(s, c)) {
                            bw.write(String.format("%s,%s,%s,%s,%s,%.2f,%d,ELIGIBLE",
                                    c.getCompanyId(), c.getCompanyName(), s.getStudentId(),
                                    s.getName(), s.getBranch(), s.getCgpa(), s.getBacklogs()));
                            bw.newLine();
                        }
                    }
                }
            } else {
                bw.write("========================================================================\n");
                bw.write("             COMPANY-SPECIFIC CANDIDATE ELIGIBILITY MATRIX              \n");
                bw.write("Generated On: " + new Date() + "\n");
                bw.write("========================================================================\n");

                for (Company c : companyList) {
                    bw.write("\nRECRUITER: " + c.getCompanyName() + " (" + c.getCompanyId() + ")\n");
                    bw.write(String.format("Benchmark -> Min CGPA: %.2f | Max Backlogs: %d | Branches: %s\n",
                            c.getMinimumCgpa(), c.getMaximumBacklogs(),
                            c.getEligibleBranches().isEmpty() ? "All" : c.getEligibleBranches()));
                    bw.write("------------------------------------------------------------------------\n");

                    int count = 0;
                    for (Student s : studentList) {
                        if (EligibilityChecker.isEligible(s, c)) {
                            bw.write(String.format("  [%s] %-20s (%-5s) | CGPA: %-4.2f | Backlogs: %d\n",
                                    s.getStudentId(), s.getName(), s.getBranch(), s.getCgpa(), s.getBacklogs()));
                            count++;
                        }
                    }
                    bw.write("  -> Total Qualified Candidates: " + count + "\n");
                }
            }
            System.out.println("Eligibility Matrix generated: " + targetPath);
        } catch (IOException e) {
            System.out.println("Failed to write eligibility matrix: " + e.getMessage());
        }
    }

    /**
     * Synchronizes live data from Java database files into the HTML dashboard.
     * Replaces static JavaScript arrays with active student and company records.
     */
    public static boolean syncLiveDashboard(ArrayList<Student> studentList, ArrayList<Company> companyList) {
        File htmlFile = new File(DASHBOARD_FILE);
        if (!htmlFile.exists()) {
            System.out.println("Notice: " + DASHBOARD_FILE + " not found in working directory.");
            return false;
        }

        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(htmlFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            String html = content.toString();

            // Construct replacement students JSON array
            StringBuilder stuJson = new StringBuilder();
            stuJson.append("const students = [\n");
            for (int i = 0; i < studentList.size(); i++) {
                Student s = studentList.get(i);
                stuJson.append(String.format(
                        "            { id: \"%s\", name: \"%s\", email: \"%s\", phone: \"%s\", branch: \"%s\", semester: %d, cgpa: %.2f, backlogs: %d }%s\n",
                        s.getStudentId(), s.getName(), s.getEmail(), s.getPhone(), s.getBranch(),
                        s.getSemester(), s.getCgpa(), s.getBacklogs(),
                        (i < studentList.size() - 1 ? "," : "")
                ));
            }
            stuJson.append("        ];");

            // Construct replacement companies JSON array
            StringBuilder compJson = new StringBuilder();
            compJson.append("const companies = [\n");
            for (int i = 0; i < companyList.size(); i++) {
                Company c = companyList.get(i);
                StringBuilder branchArr = new StringBuilder("[");
                for (int b = 0; b < c.getEligibleBranches().size(); b++) {
                    branchArr.append("\"").append(c.getEligibleBranches().get(b)).append("\"");
                    if (b < c.getEligibleBranches().size() - 1) branchArr.append(", ");
                }
                branchArr.append("]");

                compJson.append(String.format(
                        "            { id: \"%s\", name: \"%s\", minCgpa: %.2f, maxBacklogs: %d, branches: %s }%s\n",
                        c.getCompanyId(), c.getCompanyName(), c.getMinimumCgpa(), c.getMaximumBacklogs(),
                        branchArr.toString(), (i < companyList.size() - 1 ? "," : "")
                ));
            }
            compJson.append("        ];");

            // Replace arrays using regex matching
            html = html.replaceAll("(?s)const students = \\[.*?\\];", java.util.regex.Matcher.quoteReplacement(stuJson.toString()));
            html = html.replaceAll("(?s)const companies = \\[.*?\\];", java.util.regex.Matcher.quoteReplacement(compJson.toString()));

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile))) {
                writer.write(html);
            }

            System.out.println(">> Web Dashboard synchronized with live database! (" + studentList.size() + " students, " + companyList.size() + " companies).");
            System.out.println(">> Open " + DASHBOARD_FILE + " in any web browser to view updated records.");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to synchronize web dashboard: " + e.getMessage());
            return false;
        }
    }
}
