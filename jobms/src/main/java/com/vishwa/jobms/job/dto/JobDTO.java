package com.vishwa.jobms.job.dto;

import com.vishwa.jobms.job.external.Company;
import com.vishwa.jobms.job.external.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    private Long id;
    private String title;
    private String description;
    private Double minSalary;  // API-friendly
    private Double maxSalary;
    private String location;
    private Long companyId;
    private String recruiterUsername;
    private Boolean active;
    private Company company;
    private List<Review> reviews;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Pagination response inner class
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PageResponse {
        private List<JobDTO> content;
        private int currentPage;
        private long totalItems;
        private int totalPages;
    }
}
