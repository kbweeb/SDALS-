-- One-time setup for a locally installed PostgreSQL (no Docker).
-- Run from the database/ folder as the postgres superuser:
--   & "C:\Program Files\PostgreSQL\18\bin\psql.exe" -U postgres -f setup-local.sql
-- Matches the defaults in backend/src/main/resources/application.properties.

\set ON_ERROR_STOP on

CREATE ROLE smartdoor LOGIN PASSWORD 'smartdoor';
CREATE DATABASE smartdoor OWNER smartdoor;

\connect smartdoor
SET ROLE smartdoor;
\ir init/01_schema.sql

\echo 'smartdoor database ready'
