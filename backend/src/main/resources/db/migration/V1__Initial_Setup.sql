create table customers(
    id bigserial primary key,
    customer_name text not null,
    customer_email text not null,
    customer_age int not null
);