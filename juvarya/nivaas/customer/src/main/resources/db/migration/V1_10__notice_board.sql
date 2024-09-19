CREATE TABLE IF NOT EXISTS notice_board (
    id BIGINT AUTO_INCREMENT NOT NULL,
    title VARCHAR(255),
    body TEXT,
    publish_time TIMESTAMP,
    apartment_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY idx_noticeBoardid (id),
    FOREIGN KEY (apartment_id) REFERENCES nivaas_apartment(id)
);
