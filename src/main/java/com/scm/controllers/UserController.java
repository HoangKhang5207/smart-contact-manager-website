package com.scm.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserController {

    // user dashboard page
    @GetMapping("/dashboard")
    public String userDashboard() {
        return "user/dashboard";
    }

    // user profile page
    @GetMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        return "user/profile";
    }
}
