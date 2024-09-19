CREATE TABLE IF NOT EXISTS prepaid_meter (
    id BIGINT AUTO_INCREMENT NOT NULL,
    creation_time TIMESTAMP,
    cost_per_unit DOUBLE,
    description VARCHAR(255),
    name VARCHAR(255),
    nivaas_apartment_id BIGINT,
    maintenance_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY idx_prepaidid (id),
    FOREIGN KEY (nivaas_apartment_id) REFERENCES nivaas_apartment(id),
    FOREIGN KEY (maintenance_id) REFERENCES maintenance(id)
);
