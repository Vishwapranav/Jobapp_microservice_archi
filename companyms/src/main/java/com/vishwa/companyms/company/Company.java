package com.vishwa.companyms.company;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data                  // Generates getters, setters, toString(), equals(), and hashCode()
@NoArgsConstructor     // Generates a no-argument constructor (required by JPA)
@AllArgsConstructor    // Generates a constructor with all fields as arguments
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;
    private String name;
    private String description;


}