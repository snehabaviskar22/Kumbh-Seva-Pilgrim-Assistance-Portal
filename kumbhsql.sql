CREATE DATABASE kumbh_db;
USE kumbh_db;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

select * from users;

CREATE TABLE found_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255),
    item_name VARCHAR(255),
    category VARCHAR(100),
    description TEXT,
    location_found VARCHAR(255),
    datetime_found DATETIME,
    deposited_at VARCHAR(255)
);

SELECT * FROM found_items;

CREATE TABLE claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255),
    phone VARCHAR(20),
    item_id BIGINT,
    proof TEXT,
    id_type VARCHAR(50),
    id_number VARCHAR(50),
    notes TEXT,
    status VARCHAR(50) DEFAULT 'Pending'
);

select * from claims;

CREATE TABLE lost_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    full_name VARCHAR(255),
    phone_number VARCHAR(255),
    email VARCHAR(255),

    item_name VARCHAR(255),
    category VARCHAR(255),

    description VARCHAR(1000),

    location VARCHAR(255),
    date_time_lost DATETIME,

    image_url VARCHAR(500),

    age INT,
    height VARCHAR(50),
    gender VARCHAR(50),

    physical_identification VARCHAR(1000)
);

select * from lost_items;