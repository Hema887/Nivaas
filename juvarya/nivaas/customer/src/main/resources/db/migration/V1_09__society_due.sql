CREATE TABLE IF NOT EXISTS society_due (
    id BIGINT AUTO_INCREMENT NOT NULL,
    due_date DATE,
    apartment_id BIGINT,
    flat_id BIGINT,
    cost DOUBLE,
    status VARCHAR(255),
    maintenance_details TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_apartment_society_due FOREIGN KEY (apartment_id) REFERENCES nivaas_apartment(id),
    CONSTRAINT fk_flat_society_due FOREIGN KEY (flat_id) REFERENCES nivaas_flat(id),
    CONSTRAINT uc_due_apartment_flat UNIQUE (due_date, apartment_id, flat_id)
);
