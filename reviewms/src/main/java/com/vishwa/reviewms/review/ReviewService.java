package com.vishwa.reviewms.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    Page<Review> getAllReviews(Pageable pageable);

    Page<Review> getCompanyReviews(Long companyId, Pageable pageable);

    Page<Review> searchReviews(String query, Pageable pageable);

    List<Review> getReviewsByCompanyId(Long companyId);

    Page<Review> getReviewsByUsername(String username, Pageable pageable);

    boolean addReview(Long companyId, Review review);

    Review getReview(Long reviewId);

    boolean updateReviewByUser(Long reviewId, Review review, String username);

    boolean deleteReviewByUser(Long reviewId, String username);

    boolean updateReview(Long reviewId, Review review);

    boolean deleteReview(Long reviewId);

    boolean moderateReview(Long reviewId, boolean approved);

    Page<Review> getPendingReviews(Pageable pageable);
}
