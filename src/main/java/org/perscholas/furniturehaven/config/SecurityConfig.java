package org.perscholas.furniturehaven.config;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.perscholas.furniturehaven.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/register").permitAll()  // Public access
                        .requestMatchers("/homepage").hasAnyRole("CUSTOMER","ADMIN")  // Employee task hub
                        .requestMatchers("/homepage/**").hasAnyRole("CUSTOMER","ADMIN")  // Admin-only access for managing employees
                        .requestMatchers("/products/**").hasAnyRole("ADMIN","CUSTOMER")  // Admin-only access for managing tasks
                        .anyRequest().authenticated())  // All other requests require authentication

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            authentication.getAuthorities().forEach(grantedAuthority -> {
                                String role = grantedAuthority.getAuthority();
                                try {
                                    System.out.println("-------->"+role);
                                    if (role.equals("ROLE_ADMIN")) {
                                        response.sendRedirect("/homepage");  // Admin redirect
                                    } else if (role.equals("ROLE_CUSTOMER")) {
                                        System.out.println("-----in--->"+role);

                                        response.sendRedirect("/homepage");  // Employee redirect
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }))
                .logout(logout -> logout.logoutSuccessUrl("/login").permitAll())
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
