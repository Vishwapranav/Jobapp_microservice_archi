package com.vishwa.jobms.job;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String description;

    @DecimalMin("0.0")
    private BigDecimal minSalary;

    @DecimalMin("0.0")
    private BigDecimal maxSalary;

    @NotBlank
    private String location;

    @NotNull
    @Column(name = "company_id")
    private Long companyId;

    @NotBlank
    @Column(name = "recruiter_username")
    private String recruiterUsername;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
