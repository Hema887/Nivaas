INSERT INTO `nivaas_city`(`id`, `country`, `district`, `isocode`, `name`, `region`, `creation_time`) VALUES
('1','India','Visakhapatnam','IN-AP-VS','Anakapalle','Andhra Pradesh', NOW()),
('2','India','Bangalore Urban','IN-KA','Bangalore','Karnataka', NOW()),
('3','India','Visakhapatnam','IN-AP-VS','Visakhapatnam','Andhra Pradesh', NOW()),
('4','India','Hyderabad','IN-TA-HYD','Hyderabad','Telangana', NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name);