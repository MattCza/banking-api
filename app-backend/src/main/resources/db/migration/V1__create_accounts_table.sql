CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    owner_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    balance NUMERIC(19, 2) NOT NULL,
    CONSTRAINT uk_accounts_email UNIQUE (email)
);