package com.vishwa.reviewms.review;

import java.util.List;

public interface ReviewService {

    List<Review> findAll(Long companyId);

    boolean addReview(Long companyId, Review review);

    // Parameter uses reviewId
    Review getReview(Long reviewId);

    // Parameters use reviewId
    boolean updateReview(Long reviewId, Review review);

    // Parameters use reviewId
    boolean deleteReview(Long reviewId);
}
