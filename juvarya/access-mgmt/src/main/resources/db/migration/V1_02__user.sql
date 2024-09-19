CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `FIRST_NAME` VARCHAR(255),
    `username` VARCHAR(20),
    `email` VARCHAR(50),
    `PROFILE_PICTURE` BIGINT,
    `PRIMARY_CONTACT` VARCHAR(255) NOT NULL,
    `GENDER` VARCHAR(255),
    `CREATION_TIME` DATETIME,
    `TYPE` VARCHAR(255),
    `POSTAL_CODE` BIGINT,
    `FCM_TOKEN` VARCHAR(255),
    `version` INT DEFAULT 1,
    UNIQUE KEY `idx_userid` (`id`),
    UNIQUE KEY `username_UNIQUE` (`username`),
    UNIQUE KEY `email_UNIQUE` (`email`)
);

CREATE TABLE IF NOT EXISTS `user_roles` (
    `user_id` BIGINT,
    `role_id` BIGINT,
    PRIMARY KEY (`user_id`, `role_id`),
    CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
);
