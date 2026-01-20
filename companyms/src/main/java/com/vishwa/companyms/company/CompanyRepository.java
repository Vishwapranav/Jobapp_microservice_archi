package com.vishwa.companyms.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    /**
     * Find all companies with pagination and sorting
     */
    Page<Company> findAll(Pageable pageable);
    
    /**
     * Search companies by name or description (case-insensitive)
     */
    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Company> searchCompanies(@Param("searchTerm") String searchTerm, Pageable pageable);
}
