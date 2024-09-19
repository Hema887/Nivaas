CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
    `next_val` BIGINT DEFAULT 1 PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS `roles` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    CONSTRAINT idx_roleid UNIQUE (id)
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


INSERT INTO `roles` (`id`, `name`)
SELECT * FROM (
    SELECT 1, 'ROLE_USER' UNION ALL
    SELECT 2, 'ROLE_NIVAAS_PARTNER' UNION ALL
    SELECT 3, 'ROLE_APARTMENT_PARTNER'
) AS default_roles
WHERE EXISTS (
    SELECT 1
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'roles'
);
