CREATE TABLE IF NOT EXISTS `user_group` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `CODE` VARCHAR(255),
    `PROFILE_ID` BIGINT,
    UNIQUE KEY `idx_usergroupid` (`id`)
);