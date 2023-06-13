package com.amigoscode.fullstack.customer;

import com.amigoscode.fullstack.AbstractTestcontainersUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainersUnitTest {

    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    void setUp() {
    }

    @Test
    void ExistsCustomerByEmail() {
        //Given
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.save(customer);

        //When
        boolean actual = underTest.existsCustomerByEmail(email);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void ExistsCustomerByEmailFailsEmailNotPresent() {
        //Given
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        String email2 = FAKER.internet().safeEmailAddress();
        underTest.save(customer);

        //When
        boolean actual = underTest.existsCustomerByEmail(email2);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void itShouldExistsCustomerById() {
        //Given
        String email = FAKER.internet().safeEmailAddress();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.save(customer);

        Integer id = underTest.findAll().stream().
                filter(c -> c.getEmail().equals(email)).
                map(c -> c.getId()).findFirst().orElseThrow();

        //When
        boolean actual = underTest.existsCustomerById(id);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void ExistsCustomerByIdFailsIdNotPresent() {
        //Given
        int id = -1;

        //When
        boolean actual = underTest.existsCustomerById(id);

        //Then
        assertThat(actual).isFalse();
    }
}