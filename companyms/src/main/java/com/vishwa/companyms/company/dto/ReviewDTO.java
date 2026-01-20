package com.vishwa.companyms.company.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReviewDTO {
    @JsonProperty("reviewId")
    private Long id;
    private String name;
    private String description;
    private double rating;
    private Long companyId;
}
