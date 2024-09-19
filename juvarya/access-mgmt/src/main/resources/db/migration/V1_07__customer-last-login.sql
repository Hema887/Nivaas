CREATE TABLE IF NOT EXISTS `customer_last_login` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `date` DATE,
    `customer` BIGINT,
    PRIMARY KEY (`id`),
    UNIQUE (`customer`),
    CONSTRAINT fk_customer_last_login FOREIGN KEY (`customer`) REFERENCES `user` (`id`)
);
