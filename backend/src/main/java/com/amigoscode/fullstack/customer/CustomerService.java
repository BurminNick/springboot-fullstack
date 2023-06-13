package com.amigoscode.fullstack.customer;

import com.amigoscode.fullstack.exception.DuplicateResourceException;
import com.amigoscode.fullstack.exception.RequestValidationException;
import com.amigoscode.fullstack.exception.ResourceNotFound;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers(){
        return customerDAO.selectAllCustomers();
    }

    public Customer getCustomer(Integer id){
        return customerDAO.selectCustomerById(id).orElseThrow(
                ()->new ResourceNotFound("customer with id [%s] not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){
        String email = customerRegistrationRequest.email();

        if(customerDAO.existsPersonWithEmail(email)){
            throw new DuplicateResourceException("email is already taken");
        }
        customerDAO.insertCustomer(
                new Customer(
                customerRegistrationRequest.name(),
                email,
                customerRegistrationRequest.age()
        ));
    }

    public void deleteCustomerById(Integer id) {
        if(!customerDAO.existsPersonWithId(id)){
            throw new ResourceNotFound("customer with id [%s] not found".formatted(id));
        }
        customerDAO.deleteCustomerById(id);
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest updateRequest) {
        Customer customer = getCustomer(id);
        boolean changes = false;
        if(updateRequest.name()!=null && !updateRequest.name().equals(customer.getName())){
            customer.setName(updateRequest.name());
            changes = true;
        }
        if(updateRequest.email()!=null && !updateRequest.email().equals(customer.getEmail())){
            if(customerDAO.existsPersonWithEmail(updateRequest.email())){
                throw new DuplicateResourceException("email is already taken");
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }
        if(updateRequest.age()!=null && updateRequest.age() != customer.getAge()){
            customer.setAge(updateRequest.age());
            changes = true;
        }
        if(!changes){
            throw new RequestValidationException("no changes found");
        }
        customerDAO.updateCustomer(customer);
    }
}
