import java.util.ArrayList;

public class Company {

    private String companyId;
    private String companyName;
    private double minimumCgpa;
    private int maximumBacklogs;
    private ArrayList<String> eligibleBranches;

    public Company(String companyId, String companyName, double minimumCgpa,
                   int maximumBacklogs, ArrayList<String> eligibleBranches) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.minimumCgpa = minimumCgpa;
        this.maximumBacklogs = maximumBacklogs;
        this.eligibleBranches = eligibleBranches;
    }

    public void display() {
        System.out.println("--------------------------------------------------");
        System.out.println("Company ID       : " + companyId);
        System.out.println("Company Name     : " + companyName);
        System.out.println("Minimum CGPA     : " + minimumCgpa);
        System.out.println("Maximum Backlogs : " + maximumBacklogs);
        if (eligibleBranches.isEmpty()) {
            System.out.println("Eligible Branches: All Branches");
        } else {
            System.out.println("Eligible Branches: " + eligibleBranches);
        }
        System.out.println("--------------------------------------------------");
    }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public double getMinimumCgpa() { return minimumCgpa; }
    public void setMinimumCgpa(double minimumCgpa) { this.minimumCgpa = minimumCgpa; }

    public int getMaximumBacklogs() { return maximumBacklogs; }
    public void setMaximumBacklogs(int maximumBacklogs) { this.maximumBacklogs = maximumBacklogs; }

    public ArrayList<String> getEligibleBranches() { return eligibleBranches; }
    public void setEligibleBranches(ArrayList<String> eligibleBranches) { this.eligibleBranches = eligibleBranches; }

    @Override
    public String toString() {
        String branches = String.join(";", eligibleBranches);
        return companyId + "," + companyName + "," + minimumCgpa + "," + maximumBacklogs + "," + branches;
    }
}
