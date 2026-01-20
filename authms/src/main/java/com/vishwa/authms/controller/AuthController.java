package com.vishwa.authms.controller;

import com.vishwa.authms.config.JwtTokenProvider;
import com.vishwa.authms.dto.AuthRequest;
import com.vishwa.authms.dto.AuthResponse;
import com.vishwa.authms.model.User;
import com.vishwa.authms.model.UserRole;
import com.vishwa.authms.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

    // ================================
    // JOB SEEKER REGISTRATION (PUBLIC)
    // ================================
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest signUpRequest) {

        User user = userService.registerUser(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword(),
                UserRole.JOB_SEEKER // Default role
        );

        // Authenticate right after registration
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signUpRequest.getUsername(),
                        signUpRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                user.getUsername(),
                user.getRole(),
                user.getEmail()
        ));
    }

    // ================================
    // LOGIN (ALL USERS)
    // ================================
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                user.getUsername(),
                user.getRole(),
                user.getEmail()
        ));
    }

    // ================================
    // RECRUITER REGISTRATION (ADMIN-ONLY)
    // ================================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register/recruiter")
    public ResponseEntity<?> registerRecruiter(@RequestBody AuthRequest req) {
        User user = userService.registerUser(
                req.getUsername(),
                req.getEmail(),
                req.getPassword(),
                UserRole.RECRUITER
        );

        // Optional: auto-login recruiter after creation
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(),
                        req.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                user.getUsername(),
                user.getRole(),
                user.getEmail()
        ));
    }
}
