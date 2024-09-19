INSERT INTO `postalcode`(`id`,`code`,`creation_time`, `nivaas_city_id`) VALUES
    ('1', '531001', NOW(), 1),
    ('2', '531002', NOW(),  1),
    ('3', '560100', NOW(),  2),
	('4', '530001', NOW(), 3),
	('5', '500056', NOW(), 4)
	ON DUPLICATE KEY UPDATE nivaas_city_id = VALUES(nivaas_city_id);;