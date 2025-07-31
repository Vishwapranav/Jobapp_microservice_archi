package com.vishwa.jobms.job;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity                //JPA notation to mark the class as JPA entity to store in db table. JPA will automatically map this class to table
@Data                  // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor     // Generates a no-argument constructor
@AllArgsConstructor    // Generates a constructor with all fields as arguments
//@Table(name = "job_table") //tell a new name other than the class'
public class Job {
    @Id         // Marks as the primary key for the class/table
    @GeneratedValue(strategy = GenerationType.IDENTITY)   //Auto creates ids
    private Long id;
    private String title;
    private String description;
    private String minSalary;
    private String maxSalary;
    private String location;

    private Long companyId;

}