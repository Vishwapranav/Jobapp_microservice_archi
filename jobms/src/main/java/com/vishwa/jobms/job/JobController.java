package com.vishwa.jobms.job;

import com.vishwa.jobms.job.dto.JobDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    /* ===================== PUBLIC ENDPOINTS ===================== */

    @GetMapping("/legacy")
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        return ResponseEntity.ok(jobService.findAll());
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        Sort.Direction direction =
                sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        var pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Job> jobPage = jobService.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("jobs", jobPage.getContent());
        response.put("currentPage", jobPage.getNumber());
        response.put("totalItems", jobPage.getTotalElements());
        response.put("totalPages", jobPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return jobService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<Map<String, Object>> getJobsByCompany(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        Sort.Direction direction =
                sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        var pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Job> jobPage = jobService.findByCompanyId(companyId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("jobs", jobPage.getContent());
        response.put("currentPage", jobPage.getNumber());
        response.put("totalItems", jobPage.getTotalElements());
        response.put("totalPages", jobPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchJobs(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        Sort.Direction direction =
                sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        var pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Job> jobPage = jobService.searchJobs(query, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("results", jobPage.getContent());
        response.put("currentPage", jobPage.getNumber());
        response.put("totalItems", jobPage.getTotalElements());
        response.put("totalPages", jobPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /* ===================== RECRUITER ENDPOINTS ===================== */

    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Map<String, Object>> getMyJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        String recruiterUsername = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Sort.Direction direction =
                sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        var pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Job> jobPage = jobService.findByRecruiter(recruiterUsername, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("jobs", jobPage.getContent());
        response.put("currentPage", jobPage.getNumber());
        response.put("totalItems", jobPage.getTotalElements());
        response.put("totalPages", jobPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobDTO> createJob(@Valid @RequestBody JobDTO jobDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        jobDTO.setRecruiterUsername(auth.getName());

        JobDTO createdJob = jobService.createJob(jobDTO);
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<JobDTO> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobDTO jobDTO) {

        String recruiterUsername =
                SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<JobDTO> updatedJob =
                jobService.updateJob(id, jobDTO, recruiterUsername);

        return updatedJob.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @DeleteMapping("/recruiter/{id}")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<Void> deleteJobByRecruiter(@PathVariable Long id) {

        String recruiterUsername =
                SecurityContextHolder.getContext().getAuthentication().getName();

        boolean deleted = jobService.deleteJobByRecruiter(id, recruiterUsername);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return jobService.findById(id).isPresent()
                    ? ResponseEntity.status(HttpStatus.FORBIDDEN).build()
                    : ResponseEntity.notFound().build();
        }
    }

    /* ===================== ADMIN ENDPOINTS ===================== */

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteJobAdmin(@PathVariable Long id) {

        boolean deleted = jobService.deleteJob(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobDTO> updateJobAdmin(
            @PathVariable Long id,
            @Valid @RequestBody JobDTO jobDTO) {

        Optional<JobDTO> updatedJob = jobService.updateJobAdmin(id, jobDTO);

        return updatedJob.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllJobsAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        Sort.Direction direction =
                sort.length > 1 && sort[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        var pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<Job> jobPage = jobService.findAllIncludingInactive(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("jobs", jobPage.getContent());
        response.put("currentPage", jobPage.getNumber());
        response.put("totalItems", jobPage.getTotalElements());
        response.put("totalPages", jobPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateJobStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        boolean updated = jobService.updateJobStatus(id, status);
        return updated
                ? ResponseEntity.ok("Job status updated successfully")
                : ResponseEntity.notFound().build();
    }
}

