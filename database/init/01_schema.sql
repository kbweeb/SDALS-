-- Smart Door Access Control System - PostgreSQL schema
-- Runs automatically the first time the postgres container starts (empty data volume).
-- Based on section 5.1 of the technical documentation, plus:
--   * users.username / users.password_hash so admins can log into the dashboard
--   * access_logs.raw_payload to keep the original MQTT message for debugging

-- 1. System Users
CREATE TABLE users (
    id            SERIAL PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    username      VARCHAR(50)  UNIQUE,          -- only for dashboard logins (admins)
    password_hash VARCHAR(255),                 -- BCrypt hash
    role          VARCHAR(20)  NOT NULL DEFAULT 'STUDENT'
                  CHECK (role IN ('ADMIN', 'STUDENT', 'GUEST')),
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 2. Physical Lock Credentials Mapped to Users
CREATE TABLE credentials (
    id               SERIAL PRIMARY KEY,
    user_id          INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    credential_type  VARCHAR(20) NOT NULL
                     CHECK (credential_type IN ('FINGERPRINT', 'RFID_CARD', 'PASSWORD')),
    credential_index INT NOT NULL,              -- physical slot index on the lock
    card_uid         VARCHAR(64) UNIQUE,        -- RFID hex UID if exposed via MQTT
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (credential_type, credential_index)
);

-- 3. Access & Security Audit Logs
CREATE TABLE access_logs (
    id            SERIAL PRIMARY KEY,
    user_id       INT REFERENCES users(id) ON DELETE SET NULL,
    access_method VARCHAR(20) NOT NULL
                  CHECK (access_method IN ('FINGERPRINT', 'RFID', 'PIN', 'REMOTE')),
    status        VARCHAR(20) NOT NULL
                  CHECK (status IN ('AUTHORIZED', 'DENIED', 'TAMPER')),
    raw_payload   TEXT,
    logged_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_access_logs_logged_at ON access_logs (logged_at DESC);
