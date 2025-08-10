package com.vishwa.jobms.job.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Review {
    private Long reviewId; // Changed from 'id' to 'reviewId'
    private String name;
    private String description;
    private double rating;



}
