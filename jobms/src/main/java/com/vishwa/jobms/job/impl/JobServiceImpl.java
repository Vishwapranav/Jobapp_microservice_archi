package com.vishwa.jobms.job.impl;


import com.vishwa.jobms.job.Job;
import com.vishwa.jobms.job.JobRepository;
import com.vishwa.jobms.job.JobService;
import com.vishwa.jobms.job.clients.CompanyClient;
import com.vishwa.jobms.job.clients.ReviewClient;
import com.vishwa.jobms.job.dto.JobDTO;
import com.vishwa.jobms.job.external.Company;
import com.vishwa.jobms.job.external.Review;
import com.vishwa.jobms.job.mapper.JobMapper;
import jakarta.ws.rs.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository; // Added final keyword for consistency

    @Autowired
    RestTemplate restTemplate;

    private CompanyClient companyClient;
    private ReviewClient reviewClient;


    // Modified constructor to inject CompanyRepository
    public JobServiceImpl(JobRepository jobRepository, CompanyClient companyClient, ReviewClient reviewClient) {
        this.jobRepository = jobRepository;
        this.companyClient = companyClient;
        this.reviewClient = reviewClient;
    }

    @Override
    public List<JobDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    private JobDTO convertToDto(Job job) {
        Long companyId = job.getCompanyId();
        Company company = null;
        List<Review> reviews = List.of(); // default to empty list

        if (companyId != null) {
            try {
                System.out.println("📡 Fetching company for ID: " + companyId);
                company = companyClient.getCompanyById(companyId);
                reviews = reviewClient.getReviews(companyId);
            } catch (Exception e) {
                System.err.println("⚠️ Error fetching company or reviews for ID " + companyId + ": " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ companyId is null for job ID: " + job.getId());
        }

        return JobMapper.mapToJobWithCompanyDTO(job, company, reviews);
    }





    // Changed parameter name from 'id' to 'jobId' for consistency with Job entity
    @Override // Added @Override as it implements JobService.getJobById
    public JobDTO getJobById(Long jobId){
        Job job =  jobRepository.findById(jobId).orElse(null);
        return convertToDto(job);
    }

    @Override
    public void createJob(Job job) {
        // Validate if the associated company exists
        jobRepository.save(job);
    }

    // Changed parameter name from 'id' to 'jobId' for consistency
    @Override
    public boolean deleteJobById(Long jobId) {
        try {
            jobRepository.deleteById(jobId);
            return true;
        } catch (Exception e) {
            // Log the exception for debugging purposes
            System.err.println("Error deleting job with ID " + jobId + ": " + e.getMessage());
            return false;
        }
    }

    // Changed parameter name from 'id' to 'jobId' for consistency
    @Override
    public boolean updateJob(Long jobId, Job updatedJob) {
        Optional<Job> jobOptional = jobRepository.findById(jobId);

        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setLocation(updatedJob.getLocation());



            jobRepository.save(job);
            return true;
        }
        return false;
    }
}
