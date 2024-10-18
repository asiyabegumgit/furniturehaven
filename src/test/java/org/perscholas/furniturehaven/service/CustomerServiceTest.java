package org.perscholas.furniturehaven.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setUsername("testUser");
        customer.setPassword("password");
    }

    @Test
    void testGetAllEmployees() {
        List<Customer> customers = new ArrayList<>();
        customers.add(customer);

        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getUsername());
    }

    @Test
    void testFindById_CustomerExists() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        Customer result = customerService.findById(customer.getId());

        assertEquals("testUser", result.getUsername());
    }

    @Test
    void testFindById_CustomerNotFound() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> customerService.findById(customer.getId()));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testSaveCustomer() {
        when(passwordEncoder.encode(customer.getPassword())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.saveCustomer(customer);

        assertEquals("testUser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testFindByUsername() {
        when(customerRepository.findByUsername(customer.getUsername())).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.findByUsername(customer.getUsername());

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
    }

    @Test
    void testDeleteEmployee() {
        doNothing().when(customerRepository).deleteById(customer.getId());

        assertDoesNotThrow(() -> customerService.deleteEmployee(customer.getId()));
        verify(customerRepository, times(1)).deleteById(customer.getId());
    }
}