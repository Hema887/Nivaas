CREATE TABLE IF NOT EXISTS `address` (
		`id` BIGINT AUTO_INCREMENT PRIMARY KEY,
		`contactNumber` VARCHAR(225),
		`locality` VARCHAR(255),
		`line1` VARCHAR(255),
		`line2` VARCHAR(255),
		`line3` VARCHAR(255),
		`creation_time` DATE,
		`city_id` BIGINT,
		`postalcode` VARCHAR(255),
		 UNIQUE KEY `idx_addressid` (`id`),
		 INDEX `fk_address_city_id` (`city_id`),
		 CONSTRAINT `fk_address_city_id` FOREIGN KEY (`city_id`) REFERENCES `nivaas_city`(`id`)
	);