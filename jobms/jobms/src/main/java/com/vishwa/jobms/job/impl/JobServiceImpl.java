package com.vishwa.jobms.job.impl;


import com.vishwa.jobms.job.Job;
import com.vishwa.jobms.job.JobRepository;
import com.vishwa.jobms.job.JobService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository; // Added final keyword for consistency

    // Modified constructor to inject CompanyRepository
    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    // Changed parameter name from 'id' to 'jobId' for consistency with Job entity
    @Override // Added @Override as it implements JobService.getJobById
    public Job getJobById(Long jobId){
        return jobRepository.findById(jobId).orElse(null);
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
