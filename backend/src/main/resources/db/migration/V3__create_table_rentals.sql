CREATE TABLE rentals
(
    id          BIGSERIAL PRIMARY KEY,
    customer_id BIGINT         NOT NULL,
    vehicle_id  BIGINT         NOT NULL,
    start_date  DATE           NOT NULL,
    end_date    DATE           NOT NULL,
    total_value NUMERIC(10, 2) NOT NULL CHECK (total_value > 0),
    status      VARCHAR(20)    NOT NULL CHECK (status IN ('ACTIVE', 'CANCELED', 'FINISHED')),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    canceled_at TIMESTAMP,
    deleted_at  TIMESTAMP,
    CONSTRAINT fk_rentals_customers FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT fk_rentals_vehicles FOREIGN KEY (vehicle_id) REFERENCES vehicles (id),
    CONSTRAINT ck_rentals_period CHECK (end_date >= start_date)
);

CREATE INDEX idx_rentals_customer_id ON rentals (customer_id);
CREATE INDEX idx_rentals_vehicle_id ON rentals (vehicle_id);
CREATE INDEX idx_rentals_status ON rentals (status);
CREATE INDEX idx_rentals_period ON rentals (start_date, end_date);

