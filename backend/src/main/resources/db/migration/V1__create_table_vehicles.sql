CREATE TABLE vehicles
(
    id          BIGSERIAL PRIMARY KEY,
    brand       VARCHAR(100)   NOT NULL,
    model       VARCHAR(100)   NOT NULL,
    plate       VARCHAR(20)    NOT NULL,
    year        INTEGER,
    daily_value NUMERIC(10, 2) NOT NULL CHECK (daily_value > 0),
    status      VARCHAR(20)    NOT NULL CHECK (status IN ('AVAILABLE', 'UNAVAILABLE')),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    deleted_at  TIMESTAMP,
    CONSTRAINT uk_veiculos_placa UNIQUE (plate)
);