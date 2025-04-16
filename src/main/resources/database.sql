create table address (
    id bigint primary key,
    street varchar(255) not null,
    city varchar(255) not null,
    state varchar(255) not null,
    zip_code varchar(10) not null,
    last_modified timestamp with time zone not null
);

create table customer (
    id bigint primary key,
    customer_name varchar(255) not null,
    primary_address_id bigint,
    secondary_address_id bigint,
    last_modified timestamp with time zone not null
);
