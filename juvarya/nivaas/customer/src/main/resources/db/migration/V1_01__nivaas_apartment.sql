CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
    `next_val` BIGINT DEFAULT 1 PRIMARY KEY
);

-- Insert data if the table exists
INSERT INTO `hibernate_sequence` (`next_val`)
SELECT * FROM (
    SELECT 1
) AS val
WHERE EXISTS (
    SELECT 1
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'hibernate_sequence'
);

CREATE TABLE if not exists nivaas_apartment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    approve BOOLEAN,
    code VARCHAR(255),
    description VARCHAR(255),
    total_flats INT,
    type VARCHAR(255),
    builder VARCHAR(255),
    created_by INT,
    address INT,
    UNIQUE (id)
);
