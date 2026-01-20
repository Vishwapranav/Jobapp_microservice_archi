package com.vishwa.reviewms.review.impl;

import com.vishwa.reviewms.client.CompanyClient;
import com.vishwa.reviewms.dto.ReviewDTO;
import com.vishwa.reviewms.review.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"allReviews", "companyReviews"})
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CompanyClient companyClient;

    /* ================== READ ================== */

    @Override
    @Cacheable(value = "allReviews", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public Page<Review> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "companyReviews", key = "#companyId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public Page<Review> getCompanyReviews(Long companyId, Pageable pageable) {
        return reviewRepository.findByCompanyIdAndApprovedTrue(companyId, pageable);
    }

    @Override
    public List<Review> getReviewsByCompanyId(Long companyId) {
        return reviewRepository.findByCompanyId(companyId);
    }

    @Override
    public Page<Review> getReviewsByUsername(String username, Pageable pageable) {
        return reviewRepository.findByUsername(username, pageable);
    }

    @Override
    public Page<Review> searchReviews(String query, Pageable pageable) {
        if (!StringUtils.hasText(query)) return Page.empty(pageable);
        return reviewRepository.searchReviews(query, pageable);
    }

    @Override
    public Page<Review> getPendingReviews(Pageable pageable) {
        return reviewRepository.findByApprovedFalse(pageable);
    }

    /* ================== ADD ================== */

    @Override
    @Transactional
    @CacheEvict(value = {"allReviews", "companyReviews"}, allEntries = true)
    public boolean addReview(Long companyId, Review review) {

        if (reviewRepository.existsByCompanyIdAndUsername(companyId, review.getUsername())) {
            return false; // prevent duplicate
        }

        companyClient.getCompany(companyId);

        review.setCompanyId(companyId);
        review.setApproved(false);

        Review saved = reviewRepository.save(review);

        ReviewDTO dto = ReviewDTO.builder()
                .id(saved.getReviewId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .rating(saved.getRating())
                .companyId(saved.getCompanyId())
                .build();

        companyClient.updateCompanyReviewStats(companyId, dto);

        return true;
    }

    /* ================== UPDATE ================== */

    @Override
    @Transactional
    @CacheEvict(value = {"allReviews", "companyReviews"}, allEntries = true)
    public boolean updateReviewByUser(Long reviewId, Review review, String username) {
        return reviewRepository.findById(reviewId)
                .filter(r -> r.getUsername().equals(username))
                .map(r -> {
                    r.setTitle(review.getTitle());
                    r.setDescription(review.getDescription());
                    r.setRating(review.getRating());
                    r.setApproved(false); // re-moderate
                    reviewRepository.save(r);
                    return true;
                }).orElse(false);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allReviews", "companyReviews"}, allEntries = true)
    public boolean updateReview(Long reviewId, Review review) {
        return reviewRepository.findById(reviewId)
                .map(r -> {
                    r.setTitle(review.getTitle());
                    r.setDescription(review.getDescription());
                    r.setRating(review.getRating());
                    r.setApproved(false);
                    reviewRepository.save(r);
                    return true;
                }).orElse(false);
    }

    /* ================== DELETE ================== */

    @Override
    @Transactional
    @CacheEvict(value = {"allReviews", "companyReviews"}, allEntries = true)
    public boolean deleteReviewByUser(Long reviewId, String username) {
        return reviewRepository.findById(reviewId)
                .filter(r -> r.getUsername().equals(username))
                .map(r -> {
                    reviewRepository.delete(r);
                    return true;
                }).orElse(false);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allReviews", "companyReviews"}, allEntries = true)
    public boolean deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) return false;
        reviewRepository.deleteById(reviewId);
        return true;
    }

    /* ================== MODERATE ================== */

    @Override
    @Transactional
    @CacheEvict(value = {"allReviews", "companyReviews"}, allEntries = true)
    public boolean moderateReview(Long reviewId, boolean approved) {
        return reviewRepository.findById(reviewId)
                .map(r -> {
                    r.setApproved(approved);
                    reviewRepository.saveAndFlush(r); // flush to DB immediately
                    return true;
                }).orElse(false);
    }

    /* ================== SINGLE REVIEW ================== */

    @Override
    public Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId).orElse(null);
    }
}
