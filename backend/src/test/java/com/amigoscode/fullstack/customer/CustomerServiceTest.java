package com.amigoscode.fullstack.customer;

import com.amigoscode.fullstack.exception.DuplicateResourceException;
import com.amigoscode.fullstack.exception.RequestValidationException;
import com.amigoscode.fullstack.exception.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomerDAO customerDAO;

    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO, customerDTOMapper, passwordEncoder);
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
        Customer customer = new Customer(id, "Test", "test", "foobar", 99, Gender.MALE);
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);
        //When
        CustomerDTO actual = underTest.getCustomer(id);
        //Then
        assertThat(actual).isEqualTo(expected);
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
                "Test", email, "foobar", 23, Gender.MALE);
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(false);

        String passwordHash = "qwerty";
        when(passwordEncoder.encode("foobar")).thenReturn(passwordHash);

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
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
    }

    @Test
    void itShouldThrowWhenEmailWasTaken() {
        //Given
        String email = "test";
        when(customerDAO.existsPersonWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Test", email, "foobar", 23, Gender.MALE);
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
        Customer customer = new Customer(id, "Test", "test", "foobar", 99, Gender.MALE);
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
        Customer customer = new Customer(id, "Test", "test", "foobar", 99, Gender.MALE);
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
        Customer customer = new Customer(id, "Test", "test", "foobar", 99, Gender.MALE);
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