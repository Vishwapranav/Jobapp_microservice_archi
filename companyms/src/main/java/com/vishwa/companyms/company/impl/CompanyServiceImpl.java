package com.vishwa.companyms.company.impl;


import com.vishwa.companyms.company.Company;
import com.vishwa.companyms.company.CompanyRepository;
import com.vishwa.companyms.company.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Lombok generates constructor for final fields

public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Company getCompanyById(Long companyId) { // Changed parameter name
        return companyRepository.findById(companyId).orElse(null);
    }

    @Override
    public void createCompany(Company company) {
        companyRepository.save(company);
    }

    @Override
    public boolean deleteCompany(Long companyId) { // Changed parameter name
        if (companyRepository.existsById(companyId)) {
            companyRepository.deleteById(companyId);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateCompany(Long companyId, Company updatedCompany) { // Changed parameter name
        Optional<Company> companyOptional = companyRepository.findById(companyId);
        if (companyOptional.isPresent()) {
            Company company = companyOptional.get();
            company.setName(updatedCompany.getName());
            company.setDescription(updatedCompany.getDescription());
            // Note: Relationships (jobs, reviews) are typically handled in their respective services
            // or specific methods, not usually directly updated in the Company's update method
            // unless it's a direct list replacement.

            companyRepository.save(company);
            return true;
        }
        return false;
    }
}