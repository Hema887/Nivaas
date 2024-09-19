CREATE TABLE IF NOT EXISTS maintenance (
    id BIGINT AUTO_INCREMENT NOT NULL,
    creation_time TIMESTAMP,
    notify_on INT,
    cost DOUBLE,
    nivaas_apartment_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY idx_maintenanceid (id),
    FOREIGN KEY (nivaas_apartment_id) REFERENCES nivaas_apartment(id)
);
