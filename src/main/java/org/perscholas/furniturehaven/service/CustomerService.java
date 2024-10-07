package org.perscholas.furniturehaven.service;

import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Customer> getAllEmployees() {
        return customerRepository.findAll();
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public void saveCustomer(Customer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        customerRepository.save(customer);
    }

    public Optional<Customer> findByEmail(String username) {
        return customerRepository.findByUsername(username);
    }

    public void deleteEmployee(Long id) {
        customerRepository.deleteById(id);
    }
    public Optional<Customer> getCurrentCustomer()
    {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null && authentication.getPrincipal()!=null)
            return customerRepository.findByUsername(authentication.getPrincipal().toString());
        return null;
    }
}