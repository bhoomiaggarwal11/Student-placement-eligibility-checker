import java.util.ArrayList;

public class Student {

    private String studentId;
    private String name;
    private String email;
    private String phone;
    private String branch;
    private int semester;
    private double cgpa;
    private int backlogs;

    public Student(String studentId, String name, String email, String phone,
                   String branch, int semester, double cgpa, int backlogs) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.branch = branch;
        this.semester = semester;
        this.cgpa = cgpa;
        this.backlogs = backlogs;
    }

    public void display() {
        System.out.println("--------------------------------------------------");
        System.out.println("Student ID   : " + studentId);
        System.out.println("Name         : " + name);
        System.out.println("Email        : " + email);
        System.out.println("Phone        : " + phone);
        System.out.println("Branch       : " + branch);
        System.out.println("Semester     : " + semester);
        System.out.println("CGPA         : " + cgpa);
        System.out.println("Backlogs     : " + backlogs);
        System.out.println("--------------------------------------------------");
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public int getBacklogs() { return backlogs; }
    public void setBacklogs(int backlogs) { this.backlogs = backlogs; }

    @Override
    public String toString() {
        return studentId + "," + name + "," + email + "," + phone + ","
                + branch + "," + semester + "," + cgpa + "," + backlogs;
    }
}
