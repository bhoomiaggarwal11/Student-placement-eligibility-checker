import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationService {

    private static final String LOG_FILE = "email_notifications.txt";

    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * Generates and dispatches a placement interview call letter.
     */
    public static void sendInterviewCallLetter(Student student, String companyName, String roundName,
                                              String room, String timeSlot, String date) {
        String subject = "INTERVIEW CALL LETTER: " + companyName + " - " + roundName;
        StringBuilder body = new StringBuilder();
        body.append("--------------------------------------------------------------------------------\n");
        body.append("TIMESTAMP : ").append(getTimestamp()).append("\n");
        body.append("TO        : ").append(student.getEmail()).append(" (").append(student.getName()).append(")\n");
        body.append("SUBJECT   : ").append(subject).append("\n");
        body.append("--------------------------------------------------------------------------------\n");
        body.append("Dear ").append(student.getName()).append(" (ID: ").append(student.getStudentId()).append("),\n\n");
        body.append("Congratulations! Based on your academic standing (CGPA: ").append(student.getCgpa())
                .append(", Branch: ").append(student.getBranch()).append("), you have been shortlisted for:\n\n");
        body.append("  * Company      : ").append(companyName).append("\n");
        body.append("  * Round        : ").append(roundName).append("\n");
        body.append("  * Date         : ").append(date).append("\n");
        body.append("  * Assigned Room: ").append(room).append("\n");
        body.append("  * Time Slot    : ").append(timeSlot).append("\n\n");
        body.append("INSTRUCTIONS:\n");
        body.append("1. Please bring 2 printed copies of your resume and college ID card.\n");
        body.append("2. Arrive at ").append(room).append(" at least 15 minutes prior to your slot.\n");
        body.append("3. Formal dress code is mandatory.\n\n");
        body.append("Best regards,\n");
        body.append("Training & Placement Cell\n");
        body.append("--------------------------------------------------------------------------------\n");

        System.out.println(body.toString());
        logNotification(body.toString());
        System.out.println(">> Email notification logged to file: " + LOG_FILE);
        dispatchRealEmail(student.getEmail(), subject, body.toString());
    }

    /**
     * Sends eligibility status alerts (Eligible vs Not Eligible)
     */
    public static void sendEligibilityAlert(Student student, Company company, boolean isEligible, String reason) {
        String subject = "Placement Drive Update: " + company.getCompanyName();
        StringBuilder body = new StringBuilder();
        body.append("--------------------------------------------------------------------------------\n");
        body.append("TIMESTAMP : ").append(getTimestamp()).append("\n");
        body.append("TO        : ").append(student.getEmail()).append(" (").append(student.getName()).append(")\n");
        body.append("SUBJECT   : ").append(subject).append("\n");
        body.append("--------------------------------------------------------------------------------\n");
        body.append("Dear ").append(student.getName()).append(",\n\n");

        if (isEligible) {
            body.append("You are ELIGIBLE for the upcoming campus recruitment drive by ")
                    .append(company.getCompanyName()).append(".\n")
                    .append("Kindly check your scheduler portal for round timings.\n");
        } else {
            body.append("Thank you for your interest in ").append(company.getCompanyName()).append(".\n")
                    .append("Unfortunately, you did not meet the eligibility criteria: ").append(reason).append(".\n")
                    .append("We encourage you to prepare for upcoming drives.\n");
        }

        body.append("\nBest regards,\nTraining & Placement Cell\n");
        body.append("--------------------------------------------------------------------------------\n");

        System.out.println(body.toString());
        logNotification(body.toString());
        System.out.println(">> Status alert logged to file: " + LOG_FILE);
        dispatchRealEmail(student.getEmail(), subject, body.toString());
    }

    /**
     * Dispatches call letters to all eligible candidates who have scheduled slots
     */
    public static int broadcastSchedules(ArrayList<InterviewScheduler.InterviewSlot> slots, StudentManager studentManager) {
        int count = 0;
        for (InterviewScheduler.InterviewSlot slot : slots) {
            Student s = studentManager.findStudentById(slot.getStudentId());
            if (s != null) {
                sendInterviewCallLetter(s, slot.getCompanyName(), slot.getRoundName(),
                        slot.getRoom(), slot.getTimeSlot(), slot.getDate());
                count++;
            }
        }
        return count;
    }

    private static synchronized void logNotification(String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true));
            writer.write(message);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving email log: " + e.getMessage());
        }
    }

    /**
     * Bridges to the Python SMTP Real Email Sender
     */
    private static void dispatchRealEmail(String toEmail, String subject, String bodyText) {
        try {
            File tempBody = File.createTempFile("mail_body_", ".txt");
            BufferedWriter tempWriter = new BufferedWriter(new FileWriter(tempBody));
            tempWriter.write(bodyText);
            tempWriter.close();

            ProcessBuilder pb = new ProcessBuilder("python", "send_real_email.py", toEmail, subject, tempBody.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("  " + line);
            }
            process.waitFor();
            tempBody.delete();
        } catch (Exception e) {
            System.out.println("  [MAIL-INFO] SMTP dispatcher notice: " + e.getMessage());
        }
    }
}
