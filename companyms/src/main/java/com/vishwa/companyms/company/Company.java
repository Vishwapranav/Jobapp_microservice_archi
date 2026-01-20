package com.vishwa.companyms.company;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;

    private String name;
    private String description;

    private int reviewCount = 0;
    private double averageRating = 0.0;

    private int jobCount = 0; // Number of jobs posted

    private boolean featured = false; // NEW: featured company
    private boolean active = true;    // NEW: active status
}
