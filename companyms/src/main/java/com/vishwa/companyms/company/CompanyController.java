package com.vishwa.companyms.company;

import com.vishwa.companyms.company.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /* ===================== PUBLIC ENDPOINTS ===================== */

    @GetMapping("/legacy")
    public ResponseEntity<List<Company>> getAllCompaniesLegacy() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "companyId,desc") String[] sort) {

        Sort.Direction direction =
                sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Company> companyPage = companyService.getAllCompanies(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("companies", companyPage.getContent());
        response.put("currentPage", companyPage.getNumber());
        response.put("totalItems", companyPage.getTotalElements());
        response.put("totalPages", companyPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCompanies(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "companyId,desc") String[] sort) {

        Sort.Direction direction =
                sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Company> searchResults = companyService.searchCompanies(query, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("results", searchResults.getContent());
        response.put("currentPage", searchResults.getNumber());
        response.put("totalItems", searchResults.getTotalElements());
        response.put("totalPages", searchResults.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long companyId) {
        Company company = companyService.getCompanyById(companyId);
        return company != null
                ? ResponseEntity.ok(company)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{companyId}/stats")
    public ResponseEntity<Map<String, Object>> getCompanyStats(@PathVariable Long companyId) {
        Map<String, Object> stats = companyService.getCompanyStatistics(companyId);
        return stats != null
                ? ResponseEntity.ok(stats)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<Company>> getTopRatedCompanies(
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(companyService.getTopRatedCompanies(limit));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Company>> getFeaturedCompanies(
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(companyService.getFeaturedCompanies(limit));
    }

    /* ===================== INTERNAL (Feign) ENDPOINTS ===================== */

    @GetMapping("/internal/{companyId}")
    public ResponseEntity<Company> getCompanyInternal(@PathVariable Long companyId) {
        Company company = companyService.getCompanyById(companyId);
        return company != null
                ? ResponseEntity.ok(company)
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/internal/{companyId}/reviews")
    public ResponseEntity<Void> updateReviewStatsInternal(
            @PathVariable Long companyId,
            @RequestBody ReviewDTO reviewDTO) {

        companyService.updateReviewStats(companyId, reviewDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/internal/{companyId}/jobs/increment")
    public ResponseEntity<Void> incrementJobCountInternal(@PathVariable Long companyId) {
        companyService.incrementJobCount(companyId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/internal/{companyId}/jobs/decrement")
    public ResponseEntity<Void> decrementJobCountInternal(@PathVariable Long companyId) {
        companyService.decrementJobCount(companyId);
        return ResponseEntity.ok().build();
    }

    /* ===================== ADMIN ENDPOINTS ===================== */

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        Company createdCompany = companyService.createCompany(company);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @PutMapping("/{companyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Company> updateCompany(
            @PathVariable Long companyId,
            @Valid @RequestBody Company company) {

        Company updatedCompany = companyService.updateCompany(companyId, company);
        return updatedCompany != null
                ? ResponseEntity.ok(updatedCompany)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{companyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long companyId) {
        return companyService.deleteCompany(companyId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{companyId}/reviews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateReviewStats(
            @PathVariable Long companyId,
            @RequestBody ReviewDTO reviewDTO) {

        companyService.updateReviewStats(companyId, reviewDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{companyId}/featured")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> toggleFeaturedStatus(
            @PathVariable Long companyId,
            @RequestParam boolean featured) {

        boolean updated = companyService.setFeaturedStatus(companyId, featured);
        return updated
                ? ResponseEntity.ok("Featured status updated successfully")
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{companyId}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> toggleActiveStatus(
            @PathVariable Long companyId,
            @RequestParam boolean active) {

        boolean updated = companyService.setActiveStatus(companyId, active);
        return updated
                ? ResponseEntity.ok("Active status updated successfully")
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllCompaniesAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "companyId,desc") String[] sort) {

        Sort.Direction direction =
                sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Company> companyPage = companyService.getAllCompaniesIncludingInactive(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("companies", companyPage.getContent());
        response.put("currentPage", companyPage.getNumber());
        response.put("totalItems", companyPage.getTotalElements());
        response.put("totalPages", companyPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> stats = companyService.getSystemStatistics();
        return ResponseEntity.ok(stats);
    }

    /* ===================== RECRUITER ENDPOINTS ===================== */

    @GetMapping("/recruiter/my-companies")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<List<Company>> getRecruiterCompanies() {
        // This assumes recruiters can see companies they've posted jobs for
        List<Company> companies = companyService.getCompaniesByRecruiter(
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication().getName()
        );
        return ResponseEntity.ok(companies);
    }
}