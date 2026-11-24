import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class StudentManager {

    private final Map<String, Student> studentRegistry;
    private static final String FILE_NAME = "students.txt";

    public StudentManager() {
        this.studentRegistry = new LinkedHashMap<>();
        loadFromFile();
    }

    private void validateStudentData(Student s) throws ValidationException {
        if (s.getStudentId() == null || s.getStudentId().trim().isEmpty()) {
            throw new ValidationException("studentId", "ID cannot be empty.");
        }
        if (s.getName() == null || s.getName().trim().length() < 2) {
            throw new ValidationException("name", "Name must be at least 2 characters.");
        }
        if (s.getCgpa() < 0.0 || s.getCgpa() > 10.0) {
            throw new ValidationException("cgpa", "CGPA must be between 0.0 and 10.0.");
        }
        if (s.getBacklogs() < 0) {
            throw new ValidationException("backlogs", "Backlog count cannot be negative.");
        }
        if (s.getSemester() < 1 || s.getSemester() > 8) {
            throw new ValidationException("semester", "Semester must be within 1 to 8.");
        }
        if (s.getEmail() == null || !s.getEmail().contains("@")) {
            throw new ValidationException("email", "A valid email address is required.");
        }
    }

    public boolean addStudent(Student student) {
        try {
            validateStudentData(student);
            String normalizedId = student.getStudentId().trim().toUpperCase();

            if (studentRegistry.containsKey(normalizedId)) {
                throw new DuplicateRecordException("Student", normalizedId);
            }

            studentRegistry.put(normalizedId, student);
            saveToFile();
            System.out.println("Success: Student record saved [" + normalizedId + "].");
            return true;
        } catch (DuplicateRecordException | ValidationException e) {
            System.out.println("Registration Error: " + e.getMessage());
            return false;
        }
    }

    public void displayAllStudents() {
        if (studentRegistry.isEmpty()) {
            System.out.println("Notice: No registered students found.");
            return;
        }
        System.out.println("\n========== REGISTERED STUDENTS (" + studentRegistry.size() + ") ==========");
        for (Student s : studentRegistry.values()) {
            s.display();
        }
    }

    public Student findStudentById(String studentId) {
        if (studentId == null) return null;
        return studentRegistry.get(studentId.trim().toUpperCase());
    }

    public boolean updateStudent(String studentId, String newName, String newEmail, String newPhone,
                                  String newBranch, int newSemester, double newCgpa, int newBacklogs) {
        String key = studentId != null ? studentId.trim().toUpperCase() : "";
        Student existing = studentRegistry.get(key);
        if (existing == null) {
            System.out.println("Lookup Error: Student ID '" + studentId + "' not found.");
            return false;
        }

        Student draft = new Student(existing.getStudentId(), newName, newEmail, newPhone,
                newBranch, newSemester, newCgpa, newBacklogs);

        try {
            validateStudentData(draft);
            existing.setName(newName);
            existing.setEmail(newEmail);
            existing.setPhone(newPhone);
            existing.setBranch(newBranch);
            existing.setSemester(newSemester);
            existing.setCgpa(newCgpa);
            existing.setBacklogs(newBacklogs);

            saveToFile();
            System.out.println("Success: Student record updated [" + key + "].");
            return true;
        } catch (ValidationException e) {
            System.out.println("Update Rejected: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStudent(String studentId) {
        String key = studentId != null ? studentId.trim().toUpperCase() : "";
        if (!studentRegistry.containsKey(key)) {
            System.out.println("Lookup Error: Student ID '" + studentId + "' not found.");
            return false;
        }
        studentRegistry.remove(key);
        saveToFile();
        System.out.println("Success: Student record removed [" + key + "].");
        return true;
    }

    public ArrayList<Student> getAllStudents() {
        return new ArrayList<>(studentRegistry.values());
    }

    public int getStudentCount() {
        return studentRegistry.size();
    }

    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Student s : studentRegistry.values()) {
                writer.write(s.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("File write error: " + e.getMessage());
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
                    String id = tokens[0].trim();
                    String name = tokens[1].trim();
                    String email = tokens[2].trim();
                    String phone = tokens[3].trim();
                    String branch = tokens[4].trim();
                    int sem = Integer.parseInt(tokens[5].trim());
                    double cgpa = Double.parseDouble(tokens[6].trim());
                    int backlogs = Integer.parseInt(tokens[7].trim());

                    Student student = new Student(id, name, email, phone, branch, sem, cgpa, backlogs);
                    studentRegistry.put(id.toUpperCase(), student);
                }
            }
        } catch (Exception e) {
            System.out.println("Notice: Could not parse previous student file: " + e.getMessage());
        }
    }
}
