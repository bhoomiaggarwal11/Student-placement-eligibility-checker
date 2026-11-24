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

    // Prepares and logs candidate interview call letter
    public static void sendInterviewCallLetter(Student student, String companyName, String roundName,
                                              String room, String timeSlot, String date) {
        String subject = "Interview Call Letter: " + companyName + " (" + roundName + ")";
        StringBuilder letter = new StringBuilder();
        letter.append("--------------------------------------------------------------------------------\n");
        letter.append("DISPATCH TIME: ").append(getTimestamp()).append("\n");
        letter.append("CANDIDATE    : ").append(student.getName()).append(" <").append(student.getEmail()).append(">\n");
        letter.append("SUBJECT      : ").append(subject).append("\n");
        letter.append("--------------------------------------------------------------------------------\n");
        letter.append("Dear ").append(student.getName()).append(" (Student ID: ").append(student.getStudentId()).append("),\n\n");
        letter.append("You have been shortlisted for the campus interview round with ").append(companyName).append(".\n\n");
        letter.append("Round Details:\n");
        letter.append("  - Recruiter : ").append(companyName).append("\n");
        letter.append("  - Round     : ").append(roundName).append("\n");
        letter.append("  - Date      : ").append(date).append("\n");
        letter.append("  - Venue     : ").append(room).append("\n");
        letter.append("  - Slot      : ").append(timeSlot).append("\n\n");
        letter.append("Guidelines:\n");
        letter.append("1. Carry your student identification card and 2 printed copies of your resume.\n");
        letter.append("2. Please arrive at the venue 15 minutes before the allocated time slot.\n");
        letter.append("3. Formal placement attire is required.\n\n");
        letter.append("Regards,\nPlacement Coordination Committee\n");
        letter.append("--------------------------------------------------------------------------------\n");

        System.out.println(letter.toString());
        appendLog(letter.toString());
        System.out.println("Notice: Notification recorded in " + LOG_FILE);
        dispatchViaPython(student.getEmail(), subject, letter.toString());
    }

    // Sends eligibility qualification status notification
    public static void sendEligibilityAlert(Student student, Company company, boolean isEligible, String reason) {
        String subject = "Drive Eligibility Update: " + company.getCompanyName();
        StringBuilder note = new StringBuilder();
        note.append("--------------------------------------------------------------------------------\n");
        note.append("DISPATCH TIME: ").append(getTimestamp()).append("\n");
        note.append("CANDIDATE    : ").append(student.getName()).append(" <").append(student.getEmail()).append(">\n");
        note.append("SUBJECT      : ").append(subject).append("\n");
        note.append("--------------------------------------------------------------------------------\n");
        note.append("Dear ").append(student.getName()).append(",\n\n");

        if (isEligible) {
            note.append("You satisfy the preliminary eligibility criteria for ").append(company.getCompanyName()).append(".\n")
                .append("Your profile has been queued for interview slot scheduling.\n");
        } else {
            note.append("Thank you for applying to the recruitment drive for ").append(company.getCompanyName()).append(".\n")
                .append("Current status: Does not meet required criteria (").append(reason).append(").\n")
                .append("You remain eligible for subsequent campus drives that match your profile.\n");
        }

        note.append("\nRegards,\nPlacement Coordination Committee\n");
        note.append("--------------------------------------------------------------------------------\n");

        System.out.println(note.toString());
        appendLog(note.toString());
        System.out.println("Notice: Status update recorded in " + LOG_FILE);
        dispatchViaPython(student.getEmail(), subject, note.toString());
    }

    // Sends call letters to all candidates with active booked slots
    public static int broadcastSchedules(ArrayList<InterviewScheduler.InterviewSlot> slots, StudentManager studentManager) {
        int dispatched = 0;
        for (InterviewScheduler.InterviewSlot slot : slots) {
            Student s = studentManager.findStudentById(slot.getStudentId());
            if (s != null) {
                sendInterviewCallLetter(s, slot.getCompanyName(), slot.getRoundName(),
                        slot.getRoom(), slot.getTimeSlot(), slot.getDate());
                dispatched++;
            }
        }
        return dispatched;
    }

    private static synchronized void appendLog(String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(text);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Warning: Could not write notification log: " + e.getMessage());
        }
    }

    // Calls send_real_email.py using ProcessBuilder to transmit email via SMTP
    private static void dispatchViaPython(String toEmail, String subject, String bodyText) {
        try {
            File tempBody = File.createTempFile("email_payload_", ".txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempBody))) {
                writer.write(bodyText);
            }

            ProcessBuilder pb = new ProcessBuilder("python", "send_real_email.py", toEmail, subject, tempBody.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("  " + line);
                }
            }
            process.waitFor();
            tempBody.delete();
        } catch (Exception e) {
            System.out.println("  Notice: Python dispatcher exception: " + e.getMessage());
        }
    }
}
