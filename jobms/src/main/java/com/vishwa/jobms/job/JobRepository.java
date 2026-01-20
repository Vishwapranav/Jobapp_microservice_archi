package com.vishwa.jobms.job;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByActiveTrue(Pageable pageable);

    Optional<Job> findByIdAndActiveTrue(Long id);

    @Query("""
        SELECT j FROM Job j
        WHERE j.active = true
        AND (:query IS NULL OR LOWER(j.title) LIKE %:query% OR LOWER(j.description) LIKE %:query%)
    """)
    Page<Job> searchJobs(@Param("query") String query, Pageable pageable);

    Page<Job> findByCompanyIdAndActiveTrue(Long companyId, Pageable pageable);

    Page<Job> findByRecruiterUsernameAndActiveTrue(String recruiterUsername, Pageable pageable);

    Optional<Job> findByIdAndRecruiterUsername(Long id, String recruiterUsername);
}
