CREATE TABLE IF NOT EXISTS apartment_credit_history (
    id BIGINT AUTO_INCREMENT NOT NULL,
    description VARCHAR(255),
    credit_type VARCHAR(255),
    amount DOUBLE,
    transaction_date DATE,
    updated_at TIMESTAMP,
    updated_by BIGINT,
    nivaas_apartment_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (nivaas_apartment_id) REFERENCES nivaas_apartment(id)
);
