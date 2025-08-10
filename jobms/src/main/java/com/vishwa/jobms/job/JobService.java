package com.vishwa.jobms.job;

import com.vishwa.jobms.job.dto.JobDTO;

import java.util.List;

public interface JobService {
    List<JobDTO> findAll();
    JobDTO getJobById(Long jobId);
    void createJob(Job job);
    boolean deleteJobById(Long jobId);
    boolean updateJob(Long jobId, Job updatedJob);
}
