package com.vishwa.authms.config;

import com.vishwa.authms.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements ApplicationRunner {

    private final UserService userService;

    public AdminSeeder(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        userService.createAdminIfNotExists();
    }
}
