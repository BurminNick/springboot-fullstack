package com.amigoscode.fullstack.auth;

import com.amigoscode.fullstack.customer.CustomerDTO;

public record AuthenticationResponse(String token, CustomerDTO customerDTO) {
}
