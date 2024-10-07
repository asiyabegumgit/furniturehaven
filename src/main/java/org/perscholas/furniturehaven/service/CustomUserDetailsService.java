package org.perscholas.furniturehaven.service;

import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.repository.CustomerRepository;
import org.perscholas.furniturehaven.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the repository

        org.perscholas.furniturehaven.model.User user = userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Customer not found with username: " + username));


        // Manually prefix the role with 'ROLE_' if it's not already prefixed

        String role = user.getRole().name();
        String roleWithPrefix = "ROLE_" + role;
        System.out.println("-->"+role);
        // Return the user details with prefixed role
        return new User(user.getUsername(), user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix)));
    }

}
