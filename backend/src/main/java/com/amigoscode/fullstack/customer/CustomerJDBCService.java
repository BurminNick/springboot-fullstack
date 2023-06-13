package com.amigoscode.fullstack.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCService implements CustomerDAO{

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {

        var sql = """
                SELECT * FROM customers
                """;

        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT * FROM customers WHERE id = ?
                """;

        return jdbcTemplate.query(sql, customerRowMapper, id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer){
        var sql = """
                INSERT INTO customers(customer_name, customer_email, customer_age)
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = """
                SELECT count(id) FROM customers WHERE customer_email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count!=null && count>0;
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        var sql = """
                SELECT count(id) FROM customers WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count!=null && count>0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        var sql = """
                DELETE FROM customers WHERE id = ?
                """;
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void updateCustomer(Customer update) {
        if(update.getName() != null){
            String sql = "UPDATE customers SET customer_name = ? WHERE id = ?";
            jdbcTemplate.update(sql, update.getName(), update.getId());
        }
        if(update.getAge() != null){
            String sql = "UPDATE customers SET customer_age = ? WHERE id = ?";
            jdbcTemplate.update(sql, update.getAge(), update.getId());
        }
        if(update.getEmail() != null){
            String sql = "UPDATE customers SET customer_email = ? WHERE id = ?";
            jdbcTemplate.update(sql, update.getEmail(), update.getId());
        }
    }
}
