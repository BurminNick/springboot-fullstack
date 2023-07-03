package com.amigoscode.fullstack;

import com.amigoscode.fullstack.customer.Customer;
import com.amigoscode.fullstack.customer.CustomerRepository;
import com.amigoscode.fullstack.customer.Gender;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class FullstackApplication {

    public static void main(String[] args) {

        SpringApplication.run(FullstackApplication.class, args);
     }

    @Bean
    CommandLineRunner runner(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder){
        return args -> {
            var faker = new Faker();
            Random random = new Random();
            int age = random.nextInt(16, 80);
            String email = faker.internet().emailAddress();
            Customer customer = new Customer(
                    faker.name().fullName(),
                    email,
                    passwordEncoder.encode("password"),
                    age, Gender.MALE);

           customerRepository.save(customer);
            System.out.println(email);
        };
    }
}
