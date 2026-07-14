-- Create database if not exists
CREATE DATABASE userdb;

-- The table will be created automatically by Hibernate with hbm2ddl.auto=update
-- But here's the schema for reference:
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    age INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
);