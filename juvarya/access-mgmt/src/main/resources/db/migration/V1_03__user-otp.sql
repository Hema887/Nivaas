CREATE TABLE IF NOT EXISTS `user_otp` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `OTP_TYPE` VARCHAR(255),
    `CHANNEL` VARCHAR(255),
    `EMAIL_ADDRESS` VARCHAR(255),
    `user_id` BIGINT,
    `CREATION_TIME` DATETIME,
    `OTP` VARCHAR(255),
    `PRIMARY_CONTACT` VARCHAR(255),
    UNIQUE KEY `idx_userotpid` (`id`),
    INDEX `fk_userotp_user_id` (`user_id`),
    CONSTRAINT `fk_userotp_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);
