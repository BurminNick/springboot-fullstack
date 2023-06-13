package com.amigoscode.fullstack.customer;

public record CustomerRegistrationRequest (
        String name,
        String email,
        Integer age){}
