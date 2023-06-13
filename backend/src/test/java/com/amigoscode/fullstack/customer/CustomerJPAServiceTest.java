package com.amigoscode.fullstack.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class CustomerJPAServiceTest {

    private CustomerJPAService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPAService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void itShouldSelectAllCustomers() {
        //When
        underTest.selectAllCustomers();
        //Then
        Mockito.verify(customerRepository).findAll();
    }

    @Test
    void itShouldSelectCustomerById() {
        //Given
        int id = 1;
        //When
        underTest.selectCustomerById(id);
        //Then
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void itShouldInsertCustomer() {
        //Given
        Customer customer = new Customer(1, "Test", "test", 99);
        //When
        underTest.insertCustomer(customer);
        //Then
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void itShouldExistsPersonWithEmail() {
        //Given
        String email = "test";
        //When
        underTest.existsPersonWithEmail(email);
        //Then
        Mockito.verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void itShouldExistsPersonWithId() {
        //Given
        int id = 1;
        //When
        underTest.existsPersonWithId(id);
        //Then
        Mockito.verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void itShouldUpdateCustomer() {
        //Given
        Customer customer = new Customer(1, "Test", "test", 99);
        //When
        underTest.updateCustomer(customer);
        //Then
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void itShouldDeleteCustomerById() {
        //Given
        int id = 1;
        //When
        underTest.deleteCustomerById(id);
        //Then
        Mockito.verify(customerRepository).deleteById(id);
    }
}