CREATE TABLE IF NOT EXISTS `appinfo` (
	`id` BIGINT AUTO_INCREMENT PRIMARY KEY,
	`current_version` VARCHAR(20) NOT NULL
);

INSERT INTO `appinfo` (`id`, `current_version`) VALUES ('1', '1.4.3');
