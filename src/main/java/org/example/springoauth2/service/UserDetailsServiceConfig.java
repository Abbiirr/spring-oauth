package org.example.springoauth2.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserDetailsServiceConfig {

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        UserDetails user = User.withUsername("user@example.com")
                .password("{noop}password") // {noop} for plain text; use a password encoder in production
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
