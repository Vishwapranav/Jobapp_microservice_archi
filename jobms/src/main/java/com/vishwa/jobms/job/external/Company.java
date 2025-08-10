package com.vishwa.jobms.job.external;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data                  // Generates getters, setters, toString(), equals(), and hashCode()
@NoArgsConstructor     // Generates a no-argument constructor (required by JPA)
@AllArgsConstructor    // Generates a constructor with all fields as arguments
public class Company {
    private Long companyId;
    private String name;
    private String description;


}