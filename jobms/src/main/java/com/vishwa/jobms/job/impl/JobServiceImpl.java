package com.vishwa.jobms.job.impl;

import com.vishwa.jobms.job.Job;
import com.vishwa.jobms.job.JobRepository;
import com.vishwa.jobms.job.JobService;
import com.vishwa.jobms.job.dto.JobDTO;
import com.vishwa.jobms.job.mapper.JobMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Override
    public JobDTO createJob(JobDTO jobDTO) {
        Job job = JobMapper.toEntity(jobDTO);
        job.setActive(true);

        Job saved = jobRepository.save(job);
        return JobMapper.toDto(saved);
    }

    @Override
    public Optional<JobDTO> updateJob(Long id, JobDTO jobDTO, String recruiterUsername) {
        return jobRepository.findByIdAndRecruiterUsername(id, recruiterUsername)
                .map(job -> {
                    job.setTitle(jobDTO.getTitle());
                    job.setDescription(jobDTO.getDescription());
                    job.setMinSalary(JobMapper.convertDoubleToBigDecimal(jobDTO.getMinSalary()));
                    job.setMaxSalary(JobMapper.convertDoubleToBigDecimal(jobDTO.getMaxSalary()));
                    job.setLocation(jobDTO.getLocation());
                    jobRepository.save(job);
                    return JobMapper.toDto(job);
                });
    }

    @Override
    public Optional<JobDTO> updateJobAdmin(Long id, JobDTO jobDTO) {
        return jobRepository.findById(id)
                .map(job -> {
                    job.setTitle(jobDTO.getTitle());
                    job.setDescription(jobDTO.getDescription());
                    job.setMinSalary(JobMapper.convertDoubleToBigDecimal(jobDTO.getMinSalary()));
                    job.setMaxSalary(JobMapper.convertDoubleToBigDecimal(jobDTO.getMaxSalary()));
                    job.setLocation(jobDTO.getLocation());
                    job.setRecruiterUsername(jobDTO.getRecruiterUsername());
                    jobRepository.save(job);
                    return JobMapper.toDto(job);
                });
    }

    @Override
    public boolean deleteJob(Long id) {
        if (!jobRepository.existsById(id)) return false;
        jobRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteJobByRecruiter(Long id, String recruiterUsername) {
        return jobRepository.findByIdAndRecruiterUsername(id, recruiterUsername)
                .map(job -> {
                    job.setActive(false);
                    jobRepository.save(job);
                    return true;
                }).orElse(false);
    }

    @Override
    public Optional<JobDTO> findById(Long id) {
        return jobRepository.findByIdAndActiveTrue(id)
                .map(JobMapper::toDto);
    }

    @Override
    public List<JobDTO> findAll() {
        return jobRepository.findByActiveTrue(Pageable.unpaged())
                .stream()
                .map(JobMapper::toDto)
                .toList();
    }

    @Override
    public Page<Job> findAll(Pageable pageable) {
        return jobRepository.findByActiveTrue(pageable);
    }

    @Override
    public Page<Job> findAllIncludingInactive(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    @Override
    public Page<Job> searchJobs(String query, Pageable pageable) {
        return jobRepository.searchJobs(query == null ? null : query.toLowerCase(), pageable);
    }

    @Override
    public Page<Job> findByCompanyId(Long companyId, Pageable pageable) {
        return jobRepository.findByCompanyIdAndActiveTrue(companyId, pageable);
    }

    @Override
    public Page<Job> findByRecruiter(String recruiterUsername, Pageable pageable) {
        return jobRepository.findByRecruiterUsernameAndActiveTrue(recruiterUsername, pageable);
    }

    @Override
    public boolean updateJobStatus(Long id, String status) {
        return jobRepository.findById(id)
                .map(job -> {
                    job.setActive("active".equalsIgnoreCase(status));
                    jobRepository.save(job);
                    return true;
                }).orElse(false);
    }
}
