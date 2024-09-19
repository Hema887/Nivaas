CREATE TABLE IF NOT EXISTS apartment_user_role (
    id BIGINT AUTO_INCREMENT NOT NULL,
    role_name VARCHAR(255),
    nivaas_apartment_id BIGINT,
    created_by BIGINT,
    customer_id BIGINT,
    creation_time TIMESTAMP,
    approve BOOLEAN,
    PRIMARY KEY (id),
    FOREIGN KEY (nivaas_apartment_id) REFERENCES nivaas_apartment(id)
);
