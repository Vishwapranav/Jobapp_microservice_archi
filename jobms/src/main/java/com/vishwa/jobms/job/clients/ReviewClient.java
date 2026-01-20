package com.vishwa.jobms.job.clients;

import com.vishwa.jobms.job.external.Review;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "reviewms")
public interface ReviewClient {

    // ✅ INTERNAL endpoint for service-to-service call
    @GetMapping("/api/v1/reviews/internal")
    List<Review> getReviews(@RequestParam("companyId") Long companyId);
}
