package com.vishwa.reviewms.review;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Long companyId;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(nullable = false)
    private boolean approved = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
