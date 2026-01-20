package com.vishwa.jobms.job.external;

import lombok.Getter;
import lombok.Setter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long reviewId;
    private String name;
    private String description;
    private double rating;
    private Long companyId;
}
