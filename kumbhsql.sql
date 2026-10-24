-- ====================================================================
-- KumbhSeva 2027: Complete Database Schema & Initialization Script
-- Nashik Simhastha Kumbh Mela Pilgrim Assistance Portal
-- Database: kumbh_db
-- ====================================================================

CREATE DATABASE IF NOT EXISTS kumbh_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE kumbh_db;

-- 1. Users Table (Role-Based Access, Status & Volunteer Approvals)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(50) DEFAULT 'PILGRIM',      -- PILGRIM, VOLUNTEER, ADMIN
    status VARCHAR(50) DEFAULT 'APPROVED',   -- APPROVED, PENDING, REJECTED
    assigned_zone VARCHAR(255) DEFAULT 'General',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_role (role),
    INDEX idx_user_status (status)
);

-- 2. Lost Items Table (Belongings & Person Reports)
CREATE TABLE IF NOT EXISTS lost_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    full_name VARCHAR(255),
    phone_number VARCHAR(50),
    email VARCHAR(255),
    item_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    description VARCHAR(1000),
    location VARCHAR(255),
    date_time_lost DATETIME,
    image_url VARCHAR(500),
    age INT,
    height VARCHAR(50),
    gender VARCHAR(50),
    physical_identification VARCHAR(1000),
    status VARCHAR(50) DEFAULT 'Lost',       -- Lost, Under Review, Verified, Returned, Closed
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_lost_category (category),
    INDEX idx_lost_location (location),
    INDEX idx_lost_status (status),
    CONSTRAINT fk_lost_items_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 3. Found Items Table (Deposited Belongings)
CREATE TABLE IF NOT EXISTS found_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    full_name VARCHAR(255),
    phone VARCHAR(50),
    email VARCHAR(255),
    item_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    description VARCHAR(1000),
    location_found VARCHAR(255),
    datetime_found DATETIME,
    deposited_at VARCHAR(255),
    image_url VARCHAR(500),
    status VARCHAR(50) DEFAULT 'Found',      -- Found, Claimed, Returned, Closed
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_found_category (category),
    INDEX idx_found_location (location_found),
    INDEX idx_found_status (status),
    CONSTRAINT fk_found_items_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 4. Claims Table (Claim Submissions & Ownership Verification)
CREATE TABLE IF NOT EXISTS claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    user_id BIGINT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    proof VARCHAR(1000),
    id_type VARCHAR(50),
    id_number VARCHAR(100),
    notes VARCHAR(1000),
    status VARCHAR(50) DEFAULT 'Pending',    -- Pending, Approved, Rejected, Returned
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_claims_status (status),
    CONSTRAINT fk_claims_found_items FOREIGN KEY (item_id) REFERENCES found_items(id) ON DELETE CASCADE,
    CONSTRAINT fk_claims_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 5. Missing Persons Table (Dedicated Registry & Search)
CREATE TABLE IF NOT EXISTS missing_persons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lost_item_id BIGINT NULL,
    user_id BIGINT NULL,
    full_name VARCHAR(255) NOT NULL,
    age INT,
    gender VARCHAR(50),
    height VARCHAR(50),
    physical_description VARCHAR(1000),
    last_seen_location VARCHAR(255),
    last_seen_datetime DATETIME,
    contact_name VARCHAR(255),
    contact_phone VARCHAR(50),
    contact_email VARCHAR(255),
    photo_url VARCHAR(500),
    status VARCHAR(50) DEFAULT 'Missing',    -- Missing, Tracing, Located, Reunited, Closed
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_missing_status (status),
    INDEX idx_missing_location (last_seen_location),
    CONSTRAINT fk_missing_persons_lost_items FOREIGN KEY (lost_item_id) REFERENCES lost_items(id) ON DELETE CASCADE,
    CONSTRAINT fk_missing_persons_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 6. Inquiries Table (Pilgrim Contact & Support Tickets)
CREATE TABLE IF NOT EXISTS inquiries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    query_type VARCHAR(100),
    message VARCHAR(2000) NOT NULL,
    response_notes VARCHAR(2000),
    status VARCHAR(50) DEFAULT 'New',        -- New, In Progress, Resolved, Closed
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_inquiry_status (status),
    INDEX idx_inquiry_type (query_type)
);


-- Summary Verification
SELECT 'users' AS table_name, COUNT(*) AS total_rows FROM users
UNION ALL
SELECT 'lost_items', COUNT(*) FROM lost_items
UNION ALL
SELECT 'found_items', COUNT(*) FROM found_items
UNION ALL
SELECT 'claims', COUNT(*) FROM claims
UNION ALL
SELECT 'missing_persons', COUNT(*) FROM missing_persons
UNION ALL
SELECT 'inquiries', COUNT(*) FROM inquiries;