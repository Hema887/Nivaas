CREATE TABLE IF NOT EXISTS `postalcode`(
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(20) NOT NULL,
    `creation_time` DATE,  
    `nivaas_city_id` BIGINT DEFAULT NULL,
     UNIQUE KEY `idx_postalid` (`id`),
	 INDEX `fk_postalcode_nivaas_city_id` (`nivaas_city_id`),
	 CONSTRAINT `fk_postalcode_nivaas_city_id` FOREIGN KEY (`nivaas_city_id`) REFERENCES `nivaas_city`(`id`)
);
