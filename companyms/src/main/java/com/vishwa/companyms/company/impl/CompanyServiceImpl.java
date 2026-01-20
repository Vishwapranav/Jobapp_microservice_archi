package com.vishwa.companyms.company.impl;

import com.vishwa.companyms.company.Company;
import com.vishwa.companyms.company.CompanyRepository;
import com.vishwa.companyms.company.CompanyService;
import com.vishwa.companyms.company.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"companies", "companyReviews"})
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final RestTemplate restTemplate;

    private final String reviewServiceUrl = "http://localhost:8083/reviews?companyId=";

    /* =================== GET METHODS =================== */

    @Override
    @Cacheable(value = "companies", key = "'all'")
    public List<Company> getAllCompanies() {
        log.debug("Fetching all companies without pagination");
        return companyRepository.findAll();
    }

    @Override
    @Cacheable(value = "companies", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<Company> getAllCompanies(Pageable pageable) {
        log.debug("Fetching paginated companies: {}", pageable);
        return companyRepository.findAll(pageable);
    }

    @Override
    public Page<Company> getAllCompaniesIncludingInactive(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "companies", key = "'search-' + #query + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<Company> searchCompanies(String query, Pageable pageable) {
        if (!StringUtils.hasText(query) || pageable == null) {
            return Page.empty(pageable);
        }
        return companyRepository.searchCompanies(query.toLowerCase(), pageable);
    }

    @Override
    @Cacheable(value = "companies", key = "#companyId")
    public Company getCompanyById(Long companyId) {
        return companyRepository.findById(companyId).orElse(null);
    }

    @Override
    public List<ReviewDTO> getReviewsForCompany(Long companyId) {
        String url = reviewServiceUrl + companyId;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<ReviewDTO>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<ReviewDTO>>() {}
            );

            List<ReviewDTO> reviews = response.getBody();
            return reviews != null ? reviews : Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Error fetching reviews for company {}: {}", companyId, e.getMessage());
        }
        return Collections.emptyList();
    }

    /* =================== CREATE / UPDATE / DELETE =================== */

    @Override
    @Transactional
    @CacheEvict(value = {"companies"}, allEntries = true)
    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"companies"}, allEntries = true)
    public Company updateCompany(Long companyId, Company updatedCompany) {
        return companyRepository.findById(companyId).map(company -> {
            if (updatedCompany.getName() != null) company.setName(updatedCompany.getName());
            if (updatedCompany.getDescription() != null) company.setDescription(updatedCompany.getDescription());
            return companyRepository.save(company);
        }).orElse(null);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"companies"}, allEntries = true)
    public boolean deleteCompany(Long companyId) {
        if (companyRepository.existsById(companyId)) {
            companyRepository.deleteById(companyId);
            return true;
        }
        return false;
    }

    /* =================== REVIEW / JOB STATS =================== */

    @Override
    @Transactional
    @CacheEvict(value = {"companies", "companyReviews"}, allEntries = true)
    public void updateReviewStats(Long companyId, ReviewDTO reviewDTO) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        int newCount = company.getReviewCount() + 1;
        double newAverage = ((company.getAverageRating() * company.getReviewCount()) + reviewDTO.getRating()) / newCount;

        company.setReviewCount(newCount);
        company.setAverageRating(newAverage);

        companyRepository.save(company);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"companies"}, allEntries = true)
    public void incrementJobCount(Long companyId) {
        companyRepository.findById(companyId).ifPresent(company -> {
            company.setJobCount(company.getJobCount() + 1);
            companyRepository.save(company);
        });
    }

    @Override
    @Transactional
    @CacheEvict(value = {"companies"}, allEntries = true)
    public void decrementJobCount(Long companyId) {
        companyRepository.findById(companyId).ifPresent(company -> {
            company.setJobCount(Math.max(0, company.getJobCount() - 1));
            companyRepository.save(company);
        });
    }

    /* =================== FEATURED / ACTIVE =================== */

    @Override
    @Transactional
    @CacheEvict(value = {"companies"}, allEntries = true)
    public boolean setFeaturedStatus(Long companyId, boolean featured) {
        return companyRepository.findById(companyId).map(company -> {
            // Assuming you add a boolean field "featured" in Company entity
            company.setFeatured(featured);
            companyRepository.save(company);
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"companies"}, allEntries = true)
    public boolean setActiveStatus(Long companyId, boolean active) {
        return companyRepository.findById(companyId).map(company -> {
            // Assuming you add a boolean field "active" in Company entity
            company.setActive(active);
            companyRepository.save(company);
            return true;
        }).orElse(false);
    }

    /* =================== STATISTICS =================== */

    @Override
    public Map<String, Object> getCompanyStatistics(Long companyId) {
        Optional<Company> companyOpt = companyRepository.findById(companyId);

        return companyOpt
                .map(company -> Map.<String, Object>of(
                        "reviewCount", company.getReviewCount(),
                        "averageRating", company.getAverageRating(),
                        "jobCount", company.getJobCount()
                ))
                .orElse(Map.of()); // empty map if company not found
    }


    @Override
    public Map<String, Object> getSystemStatistics() {
        long totalCompanies = companyRepository.count();
        int totalJobs = companyRepository.findAll().stream().mapToInt(Company::getJobCount).sum();
        return Map.of("totalCompanies", totalCompanies, "totalJobs", totalJobs);
    }

    /* =================== RECRUITER / TOP-RATED =================== */

    @Override
    public List<Company> getCompaniesByRecruiter(String recruiterUsername) {
        // Placeholder: In reality, fetch companies where this recruiter posted jobs
        return companyRepository.findAll();
    }

    @Override
    public List<Company> getTopRatedCompanies(int limit) {
        return companyRepository.findAll().stream()
                .sorted((a, b) -> Double.compare(b.getAverageRating(), a.getAverageRating()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Company> getFeaturedCompanies(int limit) {
        return companyRepository.findAll().stream()
                .filter(Company::isFeatured) // Assuming boolean "featured" field
                .limit(limit)
                .collect(Collectors.toList());
    }
}
