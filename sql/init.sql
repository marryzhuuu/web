-- Создание последовательности
CREATE SEQUENCE IF NOT EXISTS point_seq START 1 INCREMENT 1;

-- Создание таблицы
CREATE TABLE IF NOT EXISTS point_results (
    id BIGINT PRIMARY KEY DEFAULT nextval('point_seq'),
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION NOT NULL,
    r DOUBLE PRECISION NOT NULL,
    result BOOLEAN NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    execution_time BIGINT NOT NULL
);