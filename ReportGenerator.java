import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportGenerator {

    private static final String REPORTS_DIR = "reports";

    private static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }

    private static void createReportsDirectory() {
        File dir = new File(REPORTS_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static void exportStudentReport(ArrayList<Student> studentList, String format) {
        createReportsDirectory();
        String fileName = REPORTS_DIR + "/student_report_" + getTimestamp() + "." + format.toLowerCase();

        try {
            FileWriter writer = new FileWriter(fileName);

            if (format.equalsIgnoreCase("csv")) {
                writer.write("Student ID,Name,Email,Phone,Branch,Semester,CGPA,Backlogs\n");
                for (Student s : studentList) {
                    writer.write(s.getStudentId() + ","
                            + s.getName() + ","
                            + s.getEmail() + ","
                            + s.getPhone() + ","
                            + s.getBranch() + ","
                            + s.getSemester() + ","
                            + s.getCgpa() + ","
                            + s.getBacklogs() + "\n");
                }
            } else {
                writer.write("STUDENT ACADEMIC REPORT\n");
                writer.write("Generated: " + new Date() + "\n");
                writer.write("==================================================\n");
                for (Student s : studentList) {
                    writer.write("ID: " + s.getStudentId() + " | Name: " + s.getName()
                            + " | Branch: " + s.getBranch() + " | CGPA: " + s.getCgpa()
                            + " | Backlogs: " + s.getBacklogs() + "\n");
                }
                writer.write("==================================================\n");
                writer.write("Total Students: " + studentList.size() + "\n");
            }

            writer.close();
            System.out.println("Student report saved: " + fileName);

        } catch (IOException e) {
            System.out.println("Error generating student report: " + e.getMessage());
        }
    }

    public static void exportEligibleStudentsReport(ArrayList<Company> companyList,
                                                     ArrayList<Student> studentList, String format) {
        createReportsDirectory();
        String fileName = REPORTS_DIR + "/eligible_students_report_" + getTimestamp() + "." + format.toLowerCase();

        try {
            FileWriter writer = new FileWriter(fileName);

            if (format.equalsIgnoreCase("csv")) {
                writer.write("Company ID,Company Name,Student ID,Student Name,Branch,CGPA,Backlogs\n");
                for (Company c : companyList) {
                    for (Student s : studentList) {
                        if (EligibilityChecker.isEligible(s, c)) {
                            writer.write(c.getCompanyId() + ","
                                    + c.getCompanyName() + ","
                                    + s.getStudentId() + ","
                                    + s.getName() + ","
                                    + s.getBranch() + ","
                                    + s.getCgpa() + ","
                                    + s.getBacklogs() + "\n");
                        }
                    }
                }
            } else {
                writer.write("COMPANY-WISE ELIGIBLE STUDENTS REPORT\n");
                writer.write("Generated: " + new Date() + "\n");
                writer.write("==================================================\n");
                for (Company c : companyList) {
                    writer.write("\nCompany: " + c.getCompanyName() + " (" + c.getCompanyId() + ")\n");
                    writer.write("Min CGPA: " + c.getMinimumCgpa()
                            + " | Max Backlogs: " + c.getMaximumBacklogs() + "\n");
                    writer.write("--------------------------------------------------\n");
                    int count = 0;
                    for (Student s : studentList) {
                        if (EligibilityChecker.isEligible(s, c)) {
                            writer.write("  " + s.getStudentId() + " | " + s.getName()
                                    + " | " + s.getBranch() + " | CGPA: " + s.getCgpa()
                                    + " | Backlogs: " + s.getBacklogs() + "\n");
                            count++;
                        }
                    }
                    if (count == 0) {
                        writer.write("  No eligible students.\n");
                    } else {
                        writer.write("  Eligible Count: " + count + "\n");
                    }
                }
            }

            writer.close();
            System.out.println("Eligible students report saved: " + fileName);

        } catch (IOException e) {
            System.out.println("Error generating eligible students report: " + e.getMessage());
        }
    }
}
