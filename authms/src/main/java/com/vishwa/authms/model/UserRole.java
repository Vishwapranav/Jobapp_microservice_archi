package com.vishwa.authms.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    JOB_SEEKER,
    RECRUITER,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
