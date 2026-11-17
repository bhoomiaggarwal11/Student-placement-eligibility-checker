import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class CompanyManager {

    private ArrayList<Company> companyList;
    private static final String FILE_NAME = "companies.txt";

    public CompanyManager() {
        companyList = new ArrayList<>();
        loadFromFile();
    }

    public boolean addCompany(Company company) {
        for (Company c : companyList) {
            if (c.getCompanyId().equalsIgnoreCase(company.getCompanyId())) {
                System.out.println("Error: Company ID '" + company.getCompanyId() + "' already exists.");
                return false;
            }
        }
        companyList.add(company);
        saveToFile();
        System.out.println("Company added successfully.");
        return true;
    }

    public void displayAllCompanies() {
        if (companyList.isEmpty()) {
            System.out.println("No company records found.");
            return;
        }
        System.out.println("\n========== ALL COMPANIES ==========");
        for (Company c : companyList) {
            c.display();
        }
        System.out.println("Total Companies: " + companyList.size());
    }

    public Company findCompanyById(String companyId) {
        for (Company c : companyList) {
            if (c.getCompanyId().equalsIgnoreCase(companyId)) {
                return c;
            }
        }
        return null;
    }

    public Company findCompanyByName(String name) {
        for (Company c : companyList) {
            if (c.getCompanyName().toLowerCase().contains(name.toLowerCase())) {
                return c;
            }
        }
        return null;
    }

    public boolean updateCompany(String companyId, String newName, double newMinCgpa,
                                  int newMaxBacklogs, ArrayList<String> newBranches) {
        Company company = findCompanyById(companyId);
        if (company == null) {
            System.out.println("Error: Company ID '" + companyId + "' not found.");
            return false;
        }
        company.setCompanyName(newName);
        company.setMinimumCgpa(newMinCgpa);
        company.setMaximumBacklogs(newMaxBacklogs);
        company.setEligibleBranches(newBranches);
        saveToFile();
        System.out.println("Company updated successfully.");
        return true;
    }

    public boolean deleteCompany(String companyId) {
        Company company = findCompanyById(companyId);
        if (company == null) {
            System.out.println("Error: Company ID '" + companyId + "' not found.");
            return false;
        }
        companyList.remove(company);
        saveToFile();
        System.out.println("Company deleted successfully.");
        return true;
    }

    public ArrayList<Company> getAllCompanies() {
        return companyList;
    }

    public int getCompanyCount() {
        return companyList.size();
    }

    public void saveToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
            for (Company c : companyList) {
                writer.write(c.toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving companies: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String id = parts[0];
                    String name = parts[1];
                    double minCgpa = Double.parseDouble(parts[2]);
                    int maxBacklogs = Integer.parseInt(parts[3]);
                    ArrayList<String> branches = new ArrayList<>();
                    if (parts.length == 5 && !parts[4].trim().isEmpty()) {
                        String[] branchArray = parts[4].split(";");
                        for (String b : branchArray) {
                            if (!b.trim().isEmpty()) {
                                branches.add(b.trim().toUpperCase());
                            }
                        }
                    }
                    companyList.add(new Company(id, name, minCgpa, maxBacklogs, branches));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("No existing company data found. Starting fresh.");
        }
    }
}
