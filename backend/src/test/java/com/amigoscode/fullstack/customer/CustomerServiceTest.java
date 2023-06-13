package com.amigoscode.fullstack.customer;

import com.amigoscode.fullstack.exception.DuplicateResourceException;
import com.amigoscode.fullstack.exception.RequestValidationException;
import com.amigoscode.fullstack.exception.ResourceNotFound;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDAO customerDAO;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void itShouldGetAllCustomers() {
        //When
        underTest.getAllCustomers();
        //Then
        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void itShouldGetCustomer() {
        //Given
        int id = 1;
        Customer customer = new Customer(id, "Test", "test", 99);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        Customer actual = underTest.getCustomer(id);
        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void itShouldThrowWhenGetCustomerReturnEmpty() {
        //Given
        int id = 1;
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(()->underTest.getCustomer(id)).isInstanceOf(ResourceNotFound.class).
                hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void itShouldAddCustomer() {
        //Given
        String email = "test";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Test", email, 23);
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(false);

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void itShouldThrowWhenEmailWasTaken() {
        //Given
        String email = "test";
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Test", email, 23);
        //When
        assertThatThrownBy(()->underTest.addCustomer(request)).
                isInstanceOf(DuplicateResourceException.class).
                hasMessage("email is already taken");
        //Then
        verify(customerDAO, never()).insertCustomer(any());
    }

    @Test
    void itShouldDeleteCustomerById() {
        //Given
        int id = 1;
        when(customerDAO.existsPersonWithId(id)).thenReturn(true);
        //When
        underTest.deleteCustomerById(id);
        //Then
        verify(customerDAO).deleteCustomerById(id);
    }

    @Test
    void itShouldThrowsWhenDeleteCustomerByIdNotPresent() {
        //Given
        int id = 1;
        when(customerDAO.existsPersonWithId(id)).thenReturn(false);
        //When
        assertThatThrownBy(()->underTest.deleteCustomerById(id)).isInstanceOf(ResourceNotFound.class).
                hasMessage("customer with id [%s] not found".formatted(id));
        //Then
        verify(customerDAO, never()).deleteCustomerById(id);

    }

    @Test
    void itShouldUpdateCustomer() {
        //Given
        int id = 1;
        Customer customer = new Customer(id, "Test", "test", 99);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "foo";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Foo", newEmail, 97);
        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(false);
        //When
        underTest.updateCustomer(id, updateRequest);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
    }

    @Test
    void itShouldThrowsWhenNoChanges() {
        //Given
        int id = 1;
        Customer customer = new Customer(id, "Test", "test", 99);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest updateRequest =
                new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());
        //When
        assertThatThrownBy(()->underTest.updateCustomer(id, updateRequest)).
                isInstanceOf(RequestValidationException.class).
                hasMessage("no changes found");
        //Then
        verify(customerDAO, never()).updateCustomer(any());
    }

    @Test
    void itShouldThrowsWhenEmailTaken() {
        //Given
        int id = 1;
        Customer customer = new Customer(id, "Test", "test", 99);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "new email";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null);
        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(true);
        //When
        assertThatThrownBy(()->underTest.updateCustomer(id, updateRequest)).
                isInstanceOf(DuplicateResourceException.class).
                hasMessage("email is already taken");
        //Then
        verify(customerDAO, never()).updateCustomer(any());

    }
}