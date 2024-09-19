CREATE TABLE IF NOT EXISTS apartment_flat_related_users (
    id BIGINT AUTO_INCREMENT NOT NULL,
    onboard_request_id BIGINT,
    user_id BIGINT,
    related_type VARCHAR(255) NOT NULL,
    related_user_approved BOOLEAN,
    PRIMARY KEY (id),
    UNIQUE KEY idx_related_users_id (id)
);

CREATE TABLE IF NOT EXISTS onboarding_request (
    id BIGINT AUTO_INCREMENT NOT NULL,
    status BOOLEAN,
    admin_approved BOOLEAN,
    type VARCHAR(255),
    creation_time TIMESTAMP,
    modification_time TIMESTAMP,
    approved_on TIMESTAMP,
    closed_on TIMESTAMP,
    apartment_id BIGINT,
    flat_id BIGINT,
    requested_customer BIGINT,
    PRIMARY KEY (id),
    UNIQUE KEY idx_onboardingid (id),
    CONSTRAINT fk_apartment_onboard_request FOREIGN KEY (apartment_id) REFERENCES nivaas_apartment(id),
    CONSTRAINT fk_flat_onboard_request FOREIGN KEY (flat_id) REFERENCES nivaas_flat(id)
);
