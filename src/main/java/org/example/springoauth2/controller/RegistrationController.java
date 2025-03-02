package org.example.springoauth2.controller;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private final InMemoryUserDetailsManager userDetailsManager;

    public RegistrationController(InMemoryUserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @GetMapping("/signup")
    public String showSignupForm() {
        return "signupPage";
    }


    @PostMapping("/signup")
    public String processSignup(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String email,
                                Model model) {
        if (userDetailsManager.userExists(username)) {
            model.addAttribute("error", "User already exists!");
            return "signupPage"; // Use the same view name as the GET mapping
        }

        // Create a new user with ROLE_USER
        userDetailsManager.createUser(User.withUsername(username)
                .password("{noop}" + password) // For demonstration; in production use a proper password encoder
                .roles("USER")
                .build());

        return "redirect:/login?signupSuccess";
    }

}
