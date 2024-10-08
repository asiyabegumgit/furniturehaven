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
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the repository

        Optional<Customer> optionalCustomer = customerRepository.findByUsername(username);

        if (optionalCustomer.isPresent()) {
            return new User(optionalCustomer.get().getUsername(),optionalCustomer.get().getPassword(),Collections.singleton(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        }
        // Manually prefix the role with 'ROLE_' if it's not already prefixed
        throw new UsernameNotFoundException("Customer not found");
    }

}
