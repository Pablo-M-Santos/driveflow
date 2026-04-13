CREATE TABLE customers
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(120) NOT NULL,
    cpf               VARCHAR(11)  NOT NULL,
    email             VARCHAR(120) NOT NULL,
    phone             VARCHAR(20)  NOT NULL,
    registration_date DATE         NOT NULL DEFAULT CURRENT_DATE,
    created_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP,
    deleted_at        TIMESTAMP,

    CONSTRAINT uk_customers_cpf UNIQUE (cpf),
    CONSTRAINT uk_customers_email UNIQUE (email)
);

