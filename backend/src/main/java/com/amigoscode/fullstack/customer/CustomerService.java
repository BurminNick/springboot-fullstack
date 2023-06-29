package com.amigoscode.fullstack.customer;

import com.amigoscode.fullstack.exception.DuplicateResourceException;
import com.amigoscode.fullstack.exception.RequestValidationException;
import com.amigoscode.fullstack.exception.ResourceNotFound;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDAO customerDAO;
    private final CustomerDTOMapper customerDTOMapper;
    private final PasswordEncoder passwordEncoder;


    public CustomerService(@Qualifier("jpa") CustomerDAO customerDAO, CustomerDTOMapper customerDTOMapper, PasswordEncoder passwordEncoder) {
        this.customerDAO = customerDAO;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerDAO
                .selectAllCustomers()
                .stream()
                .map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer id) {
        return customerDAO.
                selectCustomerById(id).
                map(customerDTOMapper).
                orElseThrow(
                () -> new ResourceNotFound("customer with id [%s] not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();

        if (customerDAO.existsPersonWithEmail(email)) {
            throw new DuplicateResourceException("email is already taken");
        }
        customerDAO.insertCustomer(
                new Customer(
                        customerRegistrationRequest.name(),
                        email,
                        passwordEncoder.encode(customerRegistrationRequest.password()),
                        customerRegistrationRequest.age(),
                        customerRegistrationRequest.gender()));
    }

    public void deleteCustomerById(Integer id) {
        if (!customerDAO.existsPersonWithId(id)) {
            throw new ResourceNotFound("customer with id [%s] not found".formatted(id));
        }
        customerDAO.deleteCustomerById(id);
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest updateRequest) {
        Customer customer = customerDAO.
                selectCustomerById(id).
                orElseThrow(
                        () -> new ResourceNotFound("customer with id [%s] not found".formatted(id)));

        boolean changes = false;
        if (updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
            customer.setName(updateRequest.name());
            changes = true;
        }
        if (updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
            if (customerDAO.existsPersonWithEmail(updateRequest.email())) {
                throw new DuplicateResourceException("email is already taken");
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }
        if (updateRequest.age() != null && updateRequest.age() != customer.getAge()) {
            customer.setAge(updateRequest.age());
            changes = true;
        }
        if (!changes) {
            throw new RequestValidationException("no changes found");
        }
        customerDAO.updateCustomer(customer);
    }
}
