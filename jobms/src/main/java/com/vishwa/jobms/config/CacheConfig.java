package com.vishwa.jobms.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Spring Cache.
 * Enables caching and configures cache manager with specific cache names.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configures the cache manager with specific cache names.
     * 
     * @return Configured CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "jobs",          // Cache for job listings (paginated results)
            "jobDetails",    // Cache for individual job details
            "companyJobs"    // Cache for jobs by company
        );
    }
}
