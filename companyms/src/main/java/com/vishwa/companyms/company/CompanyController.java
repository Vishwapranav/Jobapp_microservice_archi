package com.vishwa.companyms.company;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class CompanyController {

    private CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies(){
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{companyId}") // Changed path variable name
    public ResponseEntity<Company> getCompanyById(@PathVariable Long companyId){ // Changed parameter name
        Company company = companyService.getCompanyById(companyId); // Changed method call
        if (company != null){
            return new ResponseEntity<>(company, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createCompany(@RequestBody Company company){
        companyService.createCompany(company);
        return new ResponseEntity<>("Company added successfully", HttpStatus.CREATED);
    }

    @DeleteMapping("/{companyId}") // Changed path variable name
    public ResponseEntity<String> deleteCompany(@PathVariable Long companyId){ // Changed parameter name
        if (companyService.deleteCompany(companyId)) return new ResponseEntity<>("Company removed successfully", HttpStatus.OK); // Changed method call
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{companyId}") // Changed path variable name
    public ResponseEntity<String> updateCompany(@PathVariable Long companyId, @RequestBody Company company){ // Changed parameter name
        if (companyService.updateCompany(companyId, company)) return new ResponseEntity<>("Company updated successfully", HttpStatus.OK); // Changed method call
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}