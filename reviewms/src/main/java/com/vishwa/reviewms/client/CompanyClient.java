package com.vishwa.reviewms.client;

import com.vishwa.reviewms.dto.ReviewDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "companyms")
public interface CompanyClient {

    @GetMapping("/api/v1/companies/internal/{companyId}")
    Object getCompany(@PathVariable("companyId") Long companyId);

    @PutMapping("/api/v1/companies/internal/{companyId}/reviews")
    void updateCompanyReviewStats(@PathVariable("companyId") Long companyId,
                                  @RequestBody ReviewDTO reviewDTO);
}

