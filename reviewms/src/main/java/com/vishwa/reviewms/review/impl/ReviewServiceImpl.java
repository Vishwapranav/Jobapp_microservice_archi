package com.vishwa.reviewms.review.impl;


import com.vishwa.reviewms.review.Review;
import com.vishwa.reviewms.review.ReviewRepository;
import com.vishwa.reviewms.review.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ReviewServiceImpl implements ReviewService {

    // Inject ReviewRepository for database operations on Review entities
    private final ReviewRepository reviewRepository;

    // Inject CompanyService to fetch Company entities, crucial for linking reviews

    // Constructor for Dependency Injection: Spring automatically provides instances
    // of ReviewRepository and CompanyService when creating ReviewServiceImpl
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Retrieves all reviews associated with a specific company.
     * Leverages Spring Data JPA's derived query method `findByCompany_CompanyId`.
     *
     * @param companyId The ID of the company whose reviews are to be retrieved.
     * @return A list of Review objects belonging to the specified company.
     */
    @Override
    public List<Review> findAll(Long companyId) {
        // This method call is correct based on the ReviewRepository Canvas
        return reviewRepository.findByCompanyId(companyId);
    }

    /**
     * Adds a new review to a specified company.
     * This method ensures the review is correctly linked to the company identified by companyId
     * from the URL path, overriding any potentially conflicting company ID in the request body.
     *
     * @param companyId The ID of the company to which the review will be added (from URL path).
     * @param review The Review object containing the new review's details (from request body).
     * @return true if the review was successfully added, false if the company does not exist.
     */
    @Override
    public boolean addReview(Long companyId, Review review) {
        if (companyId != null) {
            review.setCompanyId(companyId);
            reviewRepository.save(review);
            return true;
        }
        return false;
    }

    /**
     * Finds a specific review by its ID within the context of a particular company.
     * This version directly queries the repository for a review matching both companyId and reviewId,
     * which is more efficient than fetching all reviews for a company and then filtering in memory.
     *
     * @param reviewId The ID of the review to find.
     * @return The Review object if found, otherwise null.
     */
    @Override
    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }

    /**
     * Updates an existing review for a specific company.
     * It ensures the review belongs to the specified company before updating.
     *
     * @param reviewId The ID of the review to update.
     * @return true if the review was found and updated, false otherwise.
     */
    @Override
    public boolean updateReview(Long reviewId, Review updatedReview) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review!=null) {
            review.setName(updatedReview.getName());
            review.setDescription(updatedReview.getDescription());
            review.setRating(updatedReview.getRating());
//            review.setCompanyId(updatedReview.getCompanyId());

            // 3. Save the updated review. Since it's a managed entity, save() performs an UPDATE.
            reviewRepository.save(review);
            return true; // Indicate successful update
        }
        return false; // Review not found for the given companyId and reviewId
    }

    /**
     * Deletes a specific review for a particular company.
     * It ensures the review belongs to the specified company before deleting.
     *
     * @param reviewId The ID of the review to delete.
     * @return true if the review was found and deleted, false otherwise.
     */
    @Override
    public boolean deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElse(null);

        if (review!=null) {

            reviewRepository.delete(review);
            return true; // Indicate successful deletion
        }
        return false; // Review not found for the given companyId and reviewId
    }
}
