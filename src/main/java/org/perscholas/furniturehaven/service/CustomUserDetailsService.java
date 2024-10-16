package org.perscholas.furniturehaven.service;

import org.perscholas.furniturehaven.model.Admin;
import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.repository.AdminRepository;
import org.perscholas.furniturehaven.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find the user as a Shopper
        Optional<Customer> optionalCustomer = customerRepository.findByUsername(username);
        if (optionalCustomer.isPresent()) {
            return new CustomUserDetails(
                    optionalCustomer.get().getUsername(),
                    optionalCustomer.get().getPassword(),
                    optionalCustomer.get().getName(),
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_CUSTOMER"))  // Correct role
            );
        }

        // Try to find the user as an AdminUser
        Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
        if (optionalAdmin.isPresent()) {
            return new CustomUserDetails(
                    optionalAdmin.get().getUsername(),
                    optionalAdmin.get().getPassword(),
                    optionalAdmin.get().getName(),
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))  // Correct role
            );
        }


        throw new UsernameNotFoundException("user not found");
    }

}
