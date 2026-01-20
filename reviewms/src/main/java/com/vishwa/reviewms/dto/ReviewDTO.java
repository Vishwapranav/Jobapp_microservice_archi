package com.vishwa.reviewms.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal rating;
    private Long companyId;
}
