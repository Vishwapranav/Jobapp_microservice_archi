package com.vishwa.jobms.job.clients;

import com.vishwa.jobms.job.external.Company;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="companyms")
public interface CompanyClient {
    @GetMapping("/companies/{companyId}")
    Company getCompanyById(@PathVariable Long companyId);
}
