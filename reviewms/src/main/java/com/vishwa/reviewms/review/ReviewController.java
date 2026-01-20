package com.vishwa.reviewms.review;

import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    private Pageable pageable(int page, int size, String[] sort) {
        Sort.Direction dir = sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(dir, sort[0]));
    }

    /* ===== PUBLIC ===== */
    @GetMapping("/public/company/{companyId}")
    public Page<Review> companyReviews(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reviewId,desc") String[] sort) {

        return reviewService.getCompanyReviews(companyId, pageable(page, size, sort));
    }

    @GetMapping("/public/search")
    public Page<Review> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reviewId,desc") String[] sort) {

        return reviewService.searchReviews(query, pageable(page, size, sort));
    }

    /* ===== USER ===== */
    @PostMapping("/company/{companyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> create(
            @PathVariable Long companyId,
            @RequestBody Review review) {

        review.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (review.getTitle() == null || review.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body("Title is required");
        }

        return reviewService.addReview(companyId, review)
                ? ResponseEntity.status(HttpStatus.CREATED).body("Review added")
                : ResponseEntity.badRequest().body("Failed to add review");
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> update(
            @PathVariable Long reviewId,
            @RequestBody Review review) {

        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return reviewService.updateReviewByUser(reviewId, review, user)
                ? ResponseEntity.ok("Updated")
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> delete(@PathVariable Long reviewId) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return reviewService.deleteReviewByUser(reviewId, user)
                ? ResponseEntity.ok("Deleted")
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /* ===== ADMIN ===== */
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Review> pending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reviewId,desc") String[] sort) {

        return reviewService.getPendingReviews(pageable(page, size, sort));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Review> allReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reviewId,desc") String[] sort) {

        return reviewService.getAllReviews(pageable(page, size, sort));
    }

    @PutMapping("/admin/{reviewId}/moderate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> moderate(
            @PathVariable Long reviewId,
            @RequestParam boolean approved) {

        return reviewService.moderateReview(reviewId, approved)
                ? ResponseEntity.ok("Moderated")
                : ResponseEntity.notFound().build();
    }
}
