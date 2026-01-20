package com.vishwa.jobms.job.mapper;

import com.vishwa.jobms.job.Job;
import com.vishwa.jobms.job.dto.JobDTO;
import com.vishwa.jobms.job.external.Company;
import com.vishwa.jobms.job.external.Review;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JobMapper {

    public static JobDTO toDto(Job job, Company company, List<Review> reviews) {
        if (job == null) return null;

        return JobDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .minSalary(convertBigDecimalToDouble(job.getMinSalary()))
                .maxSalary(convertBigDecimalToDouble(job.getMaxSalary()))
                .location(job.getLocation())
                .companyId(job.getCompanyId())
                .recruiterUsername(job.getRecruiterUsername())
                .company(company)
                .reviews(reviews != null ? reviews : Collections.emptyList())
                .active(job.getActive())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    public static JobDTO toDto(Job job) {
        return toDto(job, null, null);
    }

    public static Job toEntity(JobDTO jobDTO) {
        if (jobDTO == null) return null;

        Job job = new Job();
        job.setId(jobDTO.getId());
        job.setTitle(jobDTO.getTitle());
        job.setDescription(jobDTO.getDescription());
        job.setMinSalary(convertDoubleToBigDecimal(jobDTO.getMinSalary()));
        job.setMaxSalary(convertDoubleToBigDecimal(jobDTO.getMaxSalary()));
        job.setLocation(jobDTO.getLocation());
        job.setCompanyId(jobDTO.getCompanyId());
        job.setRecruiterUsername(jobDTO.getRecruiterUsername());
        if (jobDTO.getActive() != null) job.setActive(jobDTO.getActive());
        return job;
    }

    private static Double convertBigDecimalToDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }

    public static BigDecimal convertDoubleToBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }

    public static JobDTO.PageResponse toPageResponse(Page<Job> jobPage, Company company, List<Review> reviews) {
        List<JobDTO> content = jobPage.getContent().stream()
                .map(job -> toDto(job, company, reviews))
                .collect(Collectors.toList());

        return new JobDTO.PageResponse(
                content,
                jobPage.getNumber(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages()
        );
    }

    public static JobDTO.PageResponse toPageResponse(Page<Job> jobPage) {
        return toPageResponse(jobPage, null, null);
    }
}
