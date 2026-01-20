package com.vishwa.companyms.company;

import com.vishwa.companyms.company.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompanyService {

    // Legacy method without pagination
    List<Company> getAllCompanies();

    // Paginated method
    Page<Company> getAllCompanies(Pageable pageable);

    // Paginated including inactive (for admin)
    Page<Company> getAllCompaniesIncludingInactive(Pageable pageable);

    // Search companies
    Page<Company> searchCompanies(String query, Pageable pageable);

    // Single company
    Company getCompanyById(Long companyId);

    // Create, update, delete
    Company createCompany(Company company);
    Company updateCompany(Long companyId, Company company);
    boolean deleteCompany(Long companyId);

    // Reviews
    void updateReviewStats(Long companyId, ReviewDTO reviewDTO);
    List<ReviewDTO> getReviewsForCompany(Long companyId);

    // Job count
    void incrementJobCount(Long companyId);
    void decrementJobCount(Long companyId);

    // Featured / active toggles
    boolean setFeaturedStatus(Long companyId, boolean featured);
    boolean setActiveStatus(Long companyId, boolean active);

    // Admin stats
    java.util.Map<String, Object> getCompanyStatistics(Long companyId);
    java.util.Map<String, Object> getSystemStatistics();

    // Recruiter-specific
    List<Company> getCompaniesByRecruiter(String recruiterUsername);

    // Top-rated / featured
    List<Company> getTopRatedCompanies(int limit);
    List<Company> getFeaturedCompanies(int limit);
}
