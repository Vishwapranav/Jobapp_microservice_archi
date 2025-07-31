package com.vishwa.jobms.job;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor // Lombok generates constructor for final fields
public class JobController {

    private final JobService jobService;  // Constructor injection but lombok takes care if we use final keyword

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.findAll());
    }

    @GetMapping("/{jobId}") // Changed path variable name to jobId
    public ResponseEntity<Job> getJobById(@PathVariable Long jobId) { // Changed parameter name to jobId
        // Use the correct jobId in the service call
        return jobService.getJobById(jobId) != null
                ? ResponseEntity.ok(jobService.getJobById(jobId))
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<String> createJob(@RequestBody Job jobRequest) {
        try {
            jobService.createJob(jobRequest);
            return ResponseEntity.status(201).body("Job added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid company ID: " + e.getMessage()); // Added error message
        }
    }

    @DeleteMapping("/{jobId}") // Changed path variable name to jobId
    public ResponseEntity<String> deleteJob(@PathVariable Long jobId) { // Changed parameter name to jobId
        // Use the correct jobId in the service call
        return jobService.deleteJobById(jobId)
                ? ResponseEntity.ok("Job removed successfully")
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{jobId}") // Changed path variable name to jobId
    public ResponseEntity<String> updateJob(@PathVariable Long jobId, @RequestBody Job updatedJob) { // Changed parameter name to jobId
        // Use the correct jobId in the service call
        return jobService.updateJob(jobId, updatedJob)
                ? ResponseEntity.ok("Job updated successfully")
                : ResponseEntity.notFound().build();
    }
}
