package com.vishwa.authms.service;

import com.vishwa.authms.model.User;
import com.vishwa.authms.model.UserRole;
import com.vishwa.authms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value; // ✅ CORRECTimport org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return user;
    }

    public User registerUser(String username, String email, String password, UserRole role) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);  // ✅ must set
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        return userRepository.save(user);
    }

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public User createAdminIfNotExists() {

        return userRepository.findByUsername(adminUsername)
                .orElseGet(() ->
                        registerUser(
                                adminUsername,
                                adminEmail,
                                adminPassword,
                                UserRole.ADMIN
                        )
                );
    }

}
