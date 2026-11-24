import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CompanyManager {

    private final Map<String, Company> companyRegistry;
    private static final String FILE_NAME = "companies.txt";

    public CompanyManager() {
        this.companyRegistry = new LinkedHashMap<>();
        loadFromFile();
    }

    private void validateCompanyCriteria(Company company) throws ValidationException {
        if (company.getCompanyId() == null || company.getCompanyId().trim().isEmpty()) {
            throw new ValidationException("companyId", "Company ID cannot be blank.");
        }
        if (company.getCompanyName() == null || company.getCompanyName().trim().isEmpty()) {
            throw new ValidationException("companyName", "Company name is required.");
        }
        if (company.getMinimumCgpa() < 0.0 || company.getMinimumCgpa() > 10.0) {
            throw new ValidationException("minCgpa", "Minimum CGPA criteria must be between 0.0 and 10.0.");
        }
        if (company.getMaximumBacklogs() < 0) {
            throw new ValidationException("maxBacklogs", "Maximum allowed backlogs cannot be negative.");
        }
    }

    public boolean addCompany(Company company) {
        try {
            validateCompanyCriteria(company);
            String normalizedId = company.getCompanyId().trim().toUpperCase();

            if (companyRegistry.containsKey(normalizedId)) {
                throw new DuplicateRecordException("Company", normalizedId);
            }

            companyRegistry.put(normalizedId, company);
            saveToFile();
            System.out.println("Success: Company registered [" + normalizedId + " - " + company.getCompanyName() + "].");
            return true;
        } catch (DuplicateRecordException | ValidationException e) {
            System.out.println("Registration Rejected: " + e.getMessage());
            return false;
        }
    }

    public void displayAllCompanies() {
        if (companyRegistry.isEmpty()) {
            System.out.println("Notice: No registered companies found.");
            return;
        }
        System.out.println("\n========== PARTICIPATING RECRUITERS (" + companyRegistry.size() + ") ==========");
        for (Company c : companyRegistry.values()) {
            c.display();
        }
    }

    public Company findCompanyById(String companyId) {
        if (companyId == null) return null;
        return companyRegistry.get(companyId.trim().toUpperCase());
    }

    public Company findCompanyByName(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        String query = name.trim().toLowerCase();
        for (Company c : companyRegistry.values()) {
            if (c.getCompanyName().toLowerCase().contains(query)) {
                return c;
            }
        }
        return null;
    }

    public boolean updateCompany(String companyId, String newName, double newMinCgpa,
                                  int newMaxBacklogs, ArrayList<String> newBranches) {
        String key = companyId != null ? companyId.trim().toUpperCase() : "";
        Company existing = companyRegistry.get(key);
        if (existing == null) {
            System.out.println("Lookup Error: Company ID '" + companyId + "' not found.");
            return false;
        }

        Company draft = new Company(existing.getCompanyId(), newName, newMinCgpa, newMaxBacklogs, newBranches);
        try {
            validateCompanyCriteria(draft);
            existing.setCompanyName(newName);
            existing.setMinimumCgpa(newMinCgpa);
            existing.setMaximumBacklogs(newMaxBacklogs);
            existing.setEligibleBranches(newBranches != null ? newBranches : new ArrayList<>());

            saveToFile();
            System.out.println("Success: Company criteria updated [" + key + "].");
            return true;
        } catch (ValidationException e) {
            System.out.println("Update Rejected: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCompany(String companyId) {
        String key = companyId != null ? companyId.trim().toUpperCase() : "";
        if (!companyRegistry.containsKey(key)) {
            System.out.println("Lookup Error: Company ID '" + companyId + "' not found.");
            return false;
        }
        companyRegistry.remove(key);
        saveToFile();
        System.out.println("Success: Company record removed [" + key + "].");
        return true;
    }

    public ArrayList<Company> getAllCompanies() {
        return new ArrayList<>(companyRegistry.values());
    }

    public int getCompanyCount() {
        return companyRegistry.size();
    }

    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Company c : companyRegistry.values()) {
                writer.write(c.toString());
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
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    double minCgpa = Double.parseDouble(parts[2].trim());
                    int maxBacklogs = Integer.parseInt(parts[3].trim());
                    ArrayList<String> branches = new ArrayList<>();
                    if (parts.length >= 5 && !parts[4].trim().isEmpty()) {
                        String[] branchTokens = parts[4].split(";");
                        for (String b : branchTokens) {
                            String trimmed = b.trim();
                            if (!trimmed.isEmpty()) {
                                branches.add(trimmed.toUpperCase());
                            }
                        }
                    }
                    Company company = new Company(id, name, minCgpa, maxBacklogs, branches);
                    companyRegistry.put(id.toUpperCase(), company);
                }
            }
        } catch (Exception e) {
            System.out.println("Notice: Could not parse previous company file: " + e.getMessage());
        }
    }
}
