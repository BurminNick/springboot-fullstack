package com.amigoscode.fullstack;

import com.amigoscode.fullstack.customer.Customer;
import com.amigoscode.fullstack.customer.CustomerRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class FullstackApplication {

    public static void main(String[] args) {
        SpringApplication.run(FullstackApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args -> {
            var faker = new Faker();
            Random random = new Random();

            Customer customer = new Customer(
                    faker.name().fullName(),
                    faker.internet().emailAddress(),
                    random.nextInt(16,80));

           customerRepository.save(customer);
        };
    }
}
