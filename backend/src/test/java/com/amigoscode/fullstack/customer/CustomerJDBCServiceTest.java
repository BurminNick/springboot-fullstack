package com.amigoscode.fullstack.customer;

import com.amigoscode.fullstack.AbstractTestcontainersUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomerJDBCServiceTest extends AbstractTestcontainersUnitTest {

    private CustomerJDBCService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void itShouldSelectAllCustomers() {
        //Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                20
        );
        underTest.insertCustomer(customer);

        //When
        List<Customer> actual = underTest.selectAllCustomers();

        //Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void itShouldSelectCustomerById() {
        //Given
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);

        Integer id = underTest.selectAllCustomers().stream().
                filter(c -> c.getEmail().equals(email)).
                map(c -> c.getId()).findFirst().orElseThrow();

        //When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void itShouldReturnEmptyWhenSelectCustomerById() {
        //Given
        int id = -1;
        //When
        var actual = underTest.selectCustomerById(id);
        //Then
        assertThat(actual).isEmpty();
    }

    @Test
    void itShouldInsertCustomer() {
        //Given
        //When
        //Then
    }

    @Test
    void itShouldExistsPersonWithEmail() {
        //Given
        String email = FAKER.internet().safeEmailAddress();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );
        underTest.insertCustomer(customer);
        //When
        boolean actual = underTest.existsPersonWithEmail(email);
        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void itShouldReturnFalseWhenPersonWithEmailDoesntExist() {
        //Given
        String email = FAKER.internet().safeEmailAddress();
        //When
        boolean actual = underTest.existsPersonWithEmail(email);
        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void itShouldExistsPersonWithId() {
        //Given
        String email = FAKER.internet().safeEmailAddress();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream().
                filter(c -> c.getEmail().equals(email)).
                map(c -> c.getId()).findFirst().orElseThrow();

        //When
        boolean actual = underTest.existsPersonWithId(id);
        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void itShouldReturnFalseWhenPersonWithIdDoesntExist() {
        //Given
        int id = -1;
        //When
        boolean actual = underTest.existsPersonWithId(id);
        //Then
        assertThat(actual).isFalse();
    }


    @Test
    void itShouldDeleteCustomerById() {
        //Given
        String email = FAKER.internet().safeEmailAddress();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream().
                filter(c -> c.getEmail().equals(email)).
                map(c -> c.getId()).findFirst().orElseThrow();
        //When
        underTest.deleteCustomerById(id);
        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void itShouldUpdateCustomerNewName() {
        //Given
        String email = FAKER.internet().safeEmailAddress();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream().
                filter(c -> c.getEmail().equals(email)).
                map(c -> c.getId()).findFirst().orElseThrow();

        String newName = "foo";

        //When
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);
        underTest.updateCustomer(update);
        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c->{
           assertThat(c.getId()).isEqualTo(id);
           assertThat(c.getName()).isEqualTo(newName);
           assertThat(c.getAge()).isEqualTo(customer.getAge());
           assertThat(c.getEmail()).isEqualTo(customer.getEmail());
        });
    }

    @Test
    void itShouldUpdateCustomerNewEmail() {
        //Given
        String email = FAKER.internet().safeEmailAddress();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream().
                filter(c -> c.getEmail().equals(email)).
                map(c -> c.getId()).findFirst().orElseThrow();

        String newEmail = "foo";

        //When
        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);
        underTest.updateCustomer(update);
        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getEmail()).isEqualTo(newEmail);
        });
    }

    @Test
    void itShouldUpdateCustomerNewAge() {
        //Given
        String email = FAKER.internet().safeEmailAddress();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer id = underTest.selectAllCustomers().stream().
                filter(c -> c.getEmail().equals(email)).
                map(c -> c.getId()).findFirst().orElseThrow();

        int newAge = 55;

        //When
        Customer update = new Customer();
        update.setId(id);
        update.setAge(newAge);
        underTest.updateCustomer(update);
        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getAge()).isEqualTo(newAge);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
        });
    }
}