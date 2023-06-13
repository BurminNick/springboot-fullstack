package com.amigoscode.fullstack.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void itShouldMapRow() throws SQLException {
        //Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("customer_name")).thenReturn("Test");
        when(resultSet.getString("customer_email")).thenReturn("test");
        when(resultSet.getInt("customer_age")).thenReturn(21);
        //When
        Customer actual = customerRowMapper.mapRow(resultSet, 1);
        //Then
        Customer expected = new Customer(1, "Test", "test", 21);
        assertThat(actual).isEqualTo(expected);
    }
}