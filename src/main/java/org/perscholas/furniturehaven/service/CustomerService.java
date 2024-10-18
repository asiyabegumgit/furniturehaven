package org.perscholas.furniturehaven.service;

import lombok.extern.slf4j.Slf4j;
import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Customer> getAllEmployees() {
        log.info("Fetching all customers");
        List<Customer> customers = customerRepository.findAll();
        log.info("Retrieved {} customers", customers.size());
        return customers;
    }

    public Customer findById(Long id) {
        log.info("Fetching customer with ID {}", id);
        return customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID {}", id);
                    return new RuntimeException("Customer not found");
                });
    }

    public Customer saveCustomer(Customer customer) {
        log.info("Saving customer: {}", customer);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer saved with ID {}", savedCustomer.getId());
        return savedCustomer;
    }

    public Optional<Customer> findByUsername(String username) {
        log.info("Fetching customer with username {}", username);
        return customerRepository.findByUsername(username);
    }

    public void deleteEmployee(Long id) {
        log.info("Deleting customer with ID {}", id);
        customerRepository.deleteById(id);
        log.info("Customer with ID {} deleted", id);
    }
}