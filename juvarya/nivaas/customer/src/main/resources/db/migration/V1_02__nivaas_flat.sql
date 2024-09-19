CREATE TABLE if not exists nivaas_flat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    FLAT_NO VARCHAR(255),
    FACING VARCHAR(255),
    TOTAL_ROOMS INT,
    SQUARE_FEET DOUBLE,
    AVAILABLE_FOR_RENT BOOLEAN,
    AVAILABLE_FOR_SALE BOOLEAN,
    PARKING_AVAILABLE BOOLEAN,
    FLOOR_NO INT,
    OWNER_ID BIGINT,
    TENANT_Id BIGINT,
    nivaas_apartment_id BIGINT,
    UNIQUE (id),
    FOREIGN KEY (nivaas_apartment_id) REFERENCES nivaas_apartment(id)
);

CREATE INDEX idx_nivaas_apartment ON nivaas_flat (nivaas_apartment_id);
