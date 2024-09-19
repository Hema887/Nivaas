CREATE TABLE IF NOT EXISTS prepaid_flat_usage (
    id BIGINT AUTO_INCREMENT NOT NULL,
    creation_time TIMESTAMP,
    flat_id BIGINT,
    apartment_id BIGINT,
    units_consumed DOUBLE,
    prepaid_meter_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY uq_flat_prepaid_meter (flat_id, prepaid_meter_id),
    CONSTRAINT fk_flat_flat_usage FOREIGN KEY (flat_id) REFERENCES nivaas_flat(id),
    CONSTRAINT fk_apartment_flat_usage FOREIGN KEY (apartment_id) REFERENCES nivaas_apartment(id),
    CONSTRAINT fk_prepaid_meter_flat_usage FOREIGN KEY (prepaid_meter_id) REFERENCES prepaid_meter(id)
);
