import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class StudentManager {

    private ArrayList<Student> studentList;
    private static final String FILE_NAME = "students.txt";

    public StudentManager() {
        studentList = new ArrayList<>();
        loadFromFile();
    }

    public boolean addStudent(Student student) {
        for (Student s : studentList) {
            if (s.getStudentId().equalsIgnoreCase(student.getStudentId())) {
                System.out.println("Error: Student ID '" + student.getStudentId() + "' already exists.");
                return false;
            }
        }
        studentList.add(student);
        saveToFile();
        System.out.println("Student added successfully.");
        return true;
    }

    public void displayAllStudents() {
        if (studentList.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }
        System.out.println("\n========== ALL STUDENTS ==========");
        for (Student s : studentList) {
            s.display();
        }
        System.out.println("Total Students: " + studentList.size());
    }

    public Student findStudentById(String studentId) {
        for (Student s : studentList) {
            if (s.getStudentId().equalsIgnoreCase(studentId)) {
                return s;
            }
        }
        return null;
    }

    public boolean updateStudent(String studentId, String newName, String newEmail, String newPhone,
                                  String newBranch, int newSemester, double newCgpa, int newBacklogs) {
        Student student = findStudentById(studentId);
        if (student == null) {
            System.out.println("Error: Student ID '" + studentId + "' not found.");
            return false;
        }
        student.setName(newName);
        student.setEmail(newEmail);
        student.setPhone(newPhone);
        student.setBranch(newBranch);
        student.setSemester(newSemester);
        student.setCgpa(newCgpa);
        student.setBacklogs(newBacklogs);
        saveToFile();
        System.out.println("Student updated successfully.");
        return true;
    }

    public boolean deleteStudent(String studentId) {
        Student student = findStudentById(studentId);
        if (student == null) {
            System.out.println("Error: Student ID '" + studentId + "' not found.");
            return false;
        }
        studentList.remove(student);
        saveToFile();
        System.out.println("Student deleted successfully.");
        return true;
    }

    public ArrayList<Student> getAllStudents() {
        return studentList;
    }

    public int getStudentCount() {
        return studentList.size();
    }

    public void saveToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
            for (Student s : studentList) {
                writer.write(s.toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving students: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 8) {
                    String id = parts[0];
                    String name = parts[1];
                    String email = parts[2];
                    String phone = parts[3];
                    String branch = parts[4];
                    int semester = Integer.parseInt(parts[5]);
                    double cgpa = Double.parseDouble(parts[6]);
                    int backlogs = Integer.parseInt(parts[7]);
                    studentList.add(new Student(id, name, email, phone, branch, semester, cgpa, backlogs));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("No existing student data found. Starting fresh.");
        }
    }
}
