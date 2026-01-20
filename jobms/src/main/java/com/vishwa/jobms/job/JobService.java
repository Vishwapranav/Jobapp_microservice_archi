package com.vishwa.jobms.job;

import com.vishwa.jobms.job.dto.JobDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface JobService {

    JobDTO createJob(JobDTO jobDTO);

    Optional<JobDTO> updateJob(Long id, JobDTO jobDTO, String recruiterUsername);

    Optional<JobDTO> updateJobAdmin(Long id, JobDTO jobDTO);

    boolean deleteJob(Long id);

    boolean deleteJobByRecruiter(Long id, String recruiterUsername);

    Optional<JobDTO> findById(Long id);

    List<JobDTO> findAll(); // ✅ legacy list of all jobs

    Page<Job> findAll(Pageable pageable);

    Page<Job> findAllIncludingInactive(Pageable pageable);

    Page<Job> searchJobs(String query, Pageable pageable);

    Page<Job> findByCompanyId(Long companyId, Pageable pageable);

    Page<Job> findByRecruiter(String recruiterUsername, Pageable pageable);

    boolean updateJobStatus(Long id, String status);
}
