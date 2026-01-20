package com.vishwa.reviewms.review;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Fetch approved reviews for a company (for public viewing)
    Page<Review> findByCompanyIdAndApprovedTrue(Long companyId, Pageable pageable);

    // Fetch all reviews for a company (internal/service use)
    List<Review> findByCompanyId(Long companyId);

    // Fetch reviews by username
    Page<Review> findByUsername(String username, Pageable pageable);

    // Check if a user already added a review for a company
    boolean existsByCompanyIdAndUsername(Long companyId, String username);

    // Fetch all pending reviews (not approved)
    Page<Review> findByApprovedFalse(Pageable pageable);

    // Search reviews by title or description (approved only)
    @Query("""
        SELECT r FROM Review r
        WHERE r.approved = true
          AND (LOWER(r.title) LIKE LOWER(CONCAT('%', :term, '%'))
           OR LOWER(r.description) LIKE LOWER(CONCAT('%', :term, '%')))
    """)
    Page<Review> searchReviews(@Param("term") String term, Pageable pageable);

    // Optional: fetch all reviews for admin (approved or not)
    @Query("SELECT r FROM Review r")
    Page<Review> findAllReviews(Pageable pageable);
}
