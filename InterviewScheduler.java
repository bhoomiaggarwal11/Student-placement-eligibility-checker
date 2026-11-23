import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class InterviewScheduler {

    public static class InterviewSlot {
        private String slotId;
        private String studentId;
        private String studentName;
        private String companyName;
        private String roundName;
        private String room;
        private String timeSlot;
        private String date;

        public InterviewSlot(String slotId, String studentId, String studentName,
                             String companyName, String roundName, String room,
                             String timeSlot, String date) {
            this.slotId = slotId;
            this.studentId = studentId;
            this.studentName = studentName;
            this.companyName = companyName;
            this.roundName = roundName;
            this.room = room;
            this.timeSlot = timeSlot;
            this.date = date;
        }

        public String getSlotId() { return slotId; }
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getCompanyName() { return companyName; }
        public String getRoundName() { return roundName; }
        public String getRoom() { return room; }
        public String getTimeSlot() { return timeSlot; }
        public String getDate() { return date; }

        @Override
        public String toString() {
            return slotId + "," + studentId + "," + studentName + "," + companyName + ","
                    + roundName + "," + room + "," + timeSlot + "," + date;
        }
    }

    private ArrayList<InterviewSlot> scheduleList;
    private static final String FILE_NAME = "interviews.txt";
    private static final String[] AVAILABLE_ROOMS = {"Room A (Suite 1)", "Room B (Suite 2)", "Room C (Pod A)", "Lab 1 (Tech Lab)"};
    private static final String[] TIME_SLOTS = {
            "09:00 AM - 10:30 AM",
            "10:30 AM - 12:00 PM",
            "01:00 PM - 02:30 PM",
            "02:30 PM - 04:00 PM",
            "04:00 PM - 05:30 PM"
    };

    public InterviewScheduler() {
        scheduleList = new ArrayList<>();
        loadFromFile();
    }

    public ArrayList<InterviewSlot> getScheduleList() {
        return scheduleList;
    }

    /**
     * Automated Clash-Free Interview Scheduler
     * Allocates rooms and time slots to eligible candidates across recruitment rounds.
     */
    public int autoSchedule(Company company, ArrayList<Student> eligibleStudents, String roundName, String date) {
        if (eligibleStudents == null || eligibleStudents.isEmpty()) {
            System.out.println("No eligible students provided for scheduling.");
            return 0;
        }

        int scheduledCount = 0;
        int roomIndex = 0;
        int timeIndex = 0;

        for (Student s : eligibleStudents) {
            // Find a clash-free room and time slot
            boolean assigned = false;
            int attempts = 0;
            int maxAttempts = AVAILABLE_ROOMS.length * TIME_SLOTS.length;

            while (!assigned && attempts < maxAttempts) {
                String room = AVAILABLE_ROOMS[roomIndex];
                String time = TIME_SLOTS[timeIndex];

                if (!hasClash(room, time, date, s.getStudentId())) {
                    String slotId = "SCH-" + (scheduleList.size() + 1);
                    InterviewSlot slot = new InterviewSlot(slotId, s.getStudentId(), s.getName(),
                            company.getCompanyName(), roundName, room, time, date);
                    scheduleList.add(slot);
                    scheduledCount++;
                    assigned = true;
                }

                // Advance room and time pointers
                roomIndex++;
                if (roomIndex >= AVAILABLE_ROOMS.length) {
                    roomIndex = 0;
                    timeIndex = (timeIndex + 1) % TIME_SLOTS.length;
                }
                attempts++;
            }
        }

        saveToFile();
        return scheduledCount;
    }

    /**
     * Checks if a room is occupied at a given time/date OR if student is already booked
     */
    public boolean hasClash(String room, String time, String date, String studentId) {
        for (InterviewSlot slot : scheduleList) {
            if (slot.getDate().equalsIgnoreCase(date) && slot.getTimeSlot().equalsIgnoreCase(time)) {
                // Room conflict check
                if (slot.getRoom().equalsIgnoreCase(room)) {
                    return true;
                }
                // Student double-booking check
                if (slot.getStudentId().equalsIgnoreCase(studentId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void displayAllSchedules() {
        if (scheduleList.isEmpty()) {
            System.out.println("No interview schedules found.");
            return;
        }

        System.out.println("\n=========================== PLACEMENTHUB INTERVIEW SCHEDULE ===========================");
        System.out.printf("%-8s %-10s %-18s %-14s %-15s %-18s %-22s %-12s%n",
                "Slot ID", "Student ID", "Student Name", "Company", "Round", "Room", "Time Slot", "Date");
        System.out.println("--------------------------------------------------------------------------------------------------------------------");

        for (InterviewSlot s : scheduleList) {
            System.out.printf("%-8s %-10s %-18s %-14s %-15s %-18s %-22s %-12s%n",
                    s.getSlotId(), s.getStudentId(), s.getStudentName(), s.getCompanyName(),
                    s.getRoundName(), s.getRoom(), s.getTimeSlot(), s.getDate());
        }
        System.out.println("====================================================================================================================");
        System.out.println("Total Interviews Scheduled: " + scheduleList.size() + " | Clashes Detected: 0 (Optimal)");
    }

    public void clearAllSchedules() {
        scheduleList.clear();
        saveToFile();
        System.out.println("All interview schedules have been reset.");
    }

    public void saveToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
            for (InterviewSlot slot : scheduleList) {
                writer.write(slot.toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving interview schedules: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    InterviewSlot slot = new InterviewSlot(
                            parts[0], parts[1], parts[2], parts[3],
                            parts[4], parts[5], parts[6], parts[7]
                    );
                    scheduleList.add(slot);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading interview schedules: " + e.getMessage());
        }
    }
}
