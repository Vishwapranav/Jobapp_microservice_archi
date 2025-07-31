package com.vishwa.companyms.company;

import java.util.List;

public interface CompanyService {
    // Retrieves all company entities
    List<Company> getAllCompanies();

    // Retrieves a single company by its ID
    Company getCompanyById(Long companyId); // Parameter name changed to companyId

    // Creates a new company
    void createCompany(Company company);

    // Deletes a company by its ID
    boolean deleteCompany(Long companyId); // Parameter name changed to companyId

    // Updates an existing company
    boolean updateCompany(Long companyId, Company company); // Parameter name changed to companyId
}
