package com.amigoscode.fullstack.customer;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CustomerRowMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer(
                rs.getInt("id"),
                rs.getString("customer_name"),
                rs.getString("customer_email"),
                rs.getInt("customer_age")
        );
        return customer;
    }
}
