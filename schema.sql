-- =============================================================================
-- Expense Tracker - MySQL schema (stage 1)
-- Database: expense_db
-- Run this file after MySQL is running, e.g.:
--   mysql -u root -p < schema.sql
-- =============================================================================

-- Create the database if it does not exist, then use it.
CREATE DATABASE IF NOT EXISTS expense_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE expense_db;

-- Drop in child-to-parent order so foreign keys do not block us.
-- Safe for local/dev resets; do not run this drop sequence on production data.
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

-- -----------------------------------------------------------------------------
-- users: one row per registered person. email is unique so login is unambiguous.
-- password_hash stores a BCrypt hash (never the plain password).
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    email          VARCHAR(150) NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email)
);

-- -----------------------------------------------------------------------------
-- categories: labels like Food, Travel. Each category belongs to one user
-- so two users can both have a "Food" category without colliding.
-- ON DELETE CASCADE: if a user is removed, their categories go too.
-- -----------------------------------------------------------------------------
CREATE TABLE categories (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(80) NOT NULL,
    user_id  BIGINT NOT NULL,
    CONSTRAINT fk_categories_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------------
-- expenses: the money spent. amount is DECIMAL(10,2) so money stays exact
-- (never use FLOAT for currency). expense_date is the day of the spend;
-- created_at is when the row was inserted.
-- -----------------------------------------------------------------------------
CREATE TABLE expenses (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(150) NOT NULL,
    amount       DECIMAL(10, 2) NOT NULL,
    expense_date DATE NOT NULL,
    note         VARCHAR(500) NULL,
    category_id  BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_expenses_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    CONSTRAINT fk_expenses_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Indexes: speed up "all expenses for this user" and "expenses on/around a date".
CREATE INDEX idx_categories_user_id ON categories (user_id);
CREATE INDEX idx_expenses_user_id ON expenses (user_id);
CREATE INDEX idx_expenses_expense_date ON expenses (expense_date);

-- =============================================================================
-- Seed data (dev only)
-- password_hash below is BCrypt for the plain password: password
-- You can log in later with:  alice@example.com / password
-- =============================================================================

INSERT INTO users (name, email, password_hash) VALUES
    ('Alice Demo', 'alice@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
    ('Bob Demo',   'bob@example.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

INSERT INTO categories (name, user_id) VALUES
    ('Food',    1),
    ('Travel',  1),
    ('Bills',   1),
    ('Food',    2),
    ('Shopping', 2);

INSERT INTO expenses (title, amount, expense_date, note, category_id, user_id) VALUES
    ('Lunch at cafe',     12.50, '2026-10-20', 'Weekday lunch',           1, 1),
    ('Metro pass',        40.00, '2026-10-21', 'Monthly commute',         2, 1),
    ('Electricity',       85.75, '2026-10-22', 'October bill',            3, 1),
    ('Groceries',         54.20, '2026-10-23', NULL,                      1, 1),
    ('Taxi to airport',   18.00, '2026-10-15', 'Early morning ride',      2, 1),
    ('Team lunch',        22.00, '2026-10-18', 'Office',                  4, 2),
    ('Headphones',        59.99, '2026-10-19', 'Replacement',             5, 2);
