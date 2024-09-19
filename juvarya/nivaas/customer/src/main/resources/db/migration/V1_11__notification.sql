CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT NOT NULL,
    creation_time TIMESTAMP,
    modification_time TIMESTAMP,
    message VARCHAR(255),
    type VARCHAR(255),
    nivaas_flat_id BIGINT,
    nivaas_apartment_id BIGINT,
    user_id BIGINT,
    tenant_id BIGINT,
    onboard_request_id BIGINT,
    society_due_id BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY idx_notificationid (id),
    FOREIGN KEY (nivaas_flat_id) REFERENCES nivaas_flat(id),
    FOREIGN KEY (nivaas_apartment_id) REFERENCES nivaas_apartment(id),
    FOREIGN KEY (onboard_request_id) REFERENCES onboarding_request(id),
    FOREIGN KEY (society_due_id) REFERENCES society_due(id)
);

