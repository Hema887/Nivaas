CREATE TABLE IF NOT EXISTS current_apartment (
    id BIGINT AUTO_INCREMENT NOT NULL,
    user_id BIGINT,
    apartment_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (apartment_id) REFERENCES nivaas_apartment(id)
);
