CREATE TABLE IF NOT EXISTS `nivaas_city` (
	`id` BIGINT AUTO_INCREMENT PRIMARY KEY,
	`isocode` VARCHAR(255),
	`name` VARCHAR(255),
	`country` VARCHAR(255),
	`region` VARCHAR(255),
	`district` VARCHAR(255),
	`creation_time` DATE
);
