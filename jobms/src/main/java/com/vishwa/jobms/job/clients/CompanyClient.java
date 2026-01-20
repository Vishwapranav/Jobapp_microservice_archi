package com.vishwa.jobms.job.clients;

import com.vishwa.jobms.job.external.Company;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name="companyms")
public interface CompanyClient {

    @GetMapping("/api/v1/companies/{companyId}")
    Company getCompanyById(@PathVariable Long companyId);

    @PutMapping("/api/v1/companies/{companyId}/jobs/increment")
    void incrementJobCount(@PathVariable Long companyId);
}
