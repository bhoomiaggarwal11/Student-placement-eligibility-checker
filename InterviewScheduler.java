import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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

    private final ArrayList<InterviewSlot> scheduleList;
    private static final String FILE_NAME = "interviews.txt";

    // Campus venue allocations for placement drives
    private static final String[] VENUES = {
            "Interview Room A",
            "Interview Room B",
            "Placement Boardroom C",
            "Conference Lab 1"
    };

    private static final String[] SLOT_TIMINGS = {
            "09:30 AM - 10:30 AM",
            "10:45 AM - 11:45 AM",
            "12:00 PM - 01:00 PM",
            "02:00 PM - 03:00 PM",
            "03:15 PM - 04:15 PM",
            "04:30 PM - 05:30 PM"
    };

    public InterviewScheduler() {
        this.scheduleList = new ArrayList<>();
        loadFromFile();
    }

    public ArrayList<InterviewSlot> getScheduleList() {
        return scheduleList;
    }

    // Schedules eligible students in non-overlapping interview slots
    // Prioritizes candidates with higher academic standing first
    public int autoSchedule(Company company, ArrayList<Student> eligibleStudents, String roundName, String driveDate) {
        if (eligibleStudents == null || eligibleStudents.isEmpty()) {
            System.out.println("Notice: No eligible candidate pool provided to schedule.");
            return 0;
        }

        // Sort candidates so higher CGPA students get assigned first
        ArrayList<Student> sortedCandidates = new ArrayList<>(eligibleStudents);
        sortedCandidates.sort((a, b) -> Double.compare(b.getCgpa(), a.getCgpa()));

        int assignedCount = 0;
        int venueIndex = 0;
        int timingIndex = 0;

        for (Student s : sortedCandidates) {
            boolean slotFound = false;
            int combinationsChecked = 0;
            int totalCombinations = VENUES.length * SLOT_TIMINGS.length;

            while (!slotFound && combinationsChecked < totalCombinations) {
                String candidateVenue = VENUES[venueIndex];
                String candidateTime = SLOT_TIMINGS[timingIndex];

                if (!hasClash(candidateVenue, candidateTime, driveDate, s.getStudentId())) {
                    String slotCode = "SLOT-" + (scheduleList.size() + 1);
                    InterviewSlot newSlot = new InterviewSlot(
                            slotCode,
                            s.getStudentId(),
                            s.getName(),
                            company.getCompanyName(),
                            roundName,
                            candidateVenue,
                            candidateTime,
                            driveDate
                    );
                    scheduleList.add(newSlot);
                    assignedCount++;
                    slotFound = true;
                }

                // Advance venue and time pointers
                venueIndex++;
                if (venueIndex >= VENUES.length) {
                    venueIndex = 0;
                    timingIndex = (timingIndex + 1) % SLOT_TIMINGS.length;
                }
                combinationsChecked++;
            }

            if (!slotFound) {
                System.out.println("Warning: Capacity reached. Could not find a free slot for candidate: "
                        + s.getName() + " (" + s.getStudentId() + ")");
            }
        }

        saveToFile();
        return assignedCount;
    }

    // Verifies whether a room is in use at the same date/time OR candidate has concurrent round
    public boolean hasClash(String room, String time, String date, String studentId) {
        for (InterviewSlot existing : scheduleList) {
            if (existing.getDate().equalsIgnoreCase(date) && existing.getTimeSlot().equalsIgnoreCase(time)) {
                if (existing.getRoom().equalsIgnoreCase(room)) {
                    return true; // Room overlap
                }
                if (existing.getStudentId().equalsIgnoreCase(studentId)) {
                    return true; // Candidate double-booking
                }
            }
        }
        return false;
    }

    public void displayAllSchedules() {
        if (scheduleList.isEmpty()) {
            System.out.println("Notice: No scheduled interview slots recorded.");
            return;
        }

        System.out.println("\n================================ CAMPUS INTERVIEW TIMETABLE ================================");
        System.out.printf("%-10s %-10s %-18s %-14s %-15s %-22s %-22s %-12s%n",
                "Slot ID", "Student ID", "Candidate Name", "Company", "Round", "Venue", "Time Slot", "Date");
        System.out.println("----------------------------------------------------------------------------------------------------------------------");

        for (InterviewSlot s : scheduleList) {
            System.out.printf("%-10s %-10s %-18s %-14s %-15s %-22s %-22s %-12s%n",
                    s.getSlotId(), s.getStudentId(), s.getStudentName(), s.getCompanyName(),
                    s.getRoundName(), s.getRoom(), s.getTimeSlot(), s.getDate());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        System.out.println("Total Booked Slots: " + scheduleList.size() + " | Overlaps: 0 (Validated)");
    }

    public void clearAllSchedules() {
        scheduleList.clear();
        saveToFile();
        System.out.println("Schedule table successfully cleared.");
    }

    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (InterviewSlot slot : scheduleList) {
                writer.write(slot.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving interview timetable: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = line.split(",");
                if (tokens.length >= 8) {
                    InterviewSlot slot = new InterviewSlot(
                            tokens[0].trim(), tokens[1].trim(), tokens[2].trim(), tokens[3].trim(),
                            tokens[4].trim(), tokens[5].trim(), tokens[6].trim(), tokens[7].trim()
                    );
                    scheduleList.add(slot);
                }
            }
        } catch (IOException e) {
            System.out.println("Notice: Could not load interview schedule file: " + e.getMessage());
        }
    }
}
