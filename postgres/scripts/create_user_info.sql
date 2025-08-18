CREATE TABLE IF NOT EXISTS user_info (
    user_id VARCHAR(36) PRIMARY KEY NOT NULL,
    mobile_number VARCHAR(30) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(128) UNIQUE NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    user_password VARCHAR(255)
);

alter table user_info REPLICA IDENTITY FULL;