package org.perscholas.furniturehaven.config;

import org.perscholas.furniturehaven.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/login", "/reset-password","/forgot-password","/home", "/home/**", "/products", "/products/**", "/products/category/**", "/products/search/**", "/services", "/privacy", "/signup", "/about", "/receipt").permitAll()  // Public access
                        .requestMatchers("/logout").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers("/cart/**").hasRole("CUSTOMER")
                        .requestMatchers("/admin").hasRole("ADMIN")

                        .anyRequest().authenticated())  // All other requests require authentication

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {
                            authentication.getAuthorities().forEach(grantedAuthority -> {
                                String role = grantedAuthority.getAuthority();

                                try {

                                    if (role.equals("ROLE_ADMIN")) {
                                        response.sendRedirect("/admin");
                                    } else
                                        response.sendRedirect("/home");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }))
                //.logout(logout -> logout.logoutSuccessUrl("/login"))
                // .userDetailsService(userDetailsService);

                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // Specify the logout URL

                        .invalidateHttpSession(true) // Invalidate session
                        .clearAuthentication(true) // Clear authentication
                        .deleteCookies("JSESSIONID")
                        .permitAll())

                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
