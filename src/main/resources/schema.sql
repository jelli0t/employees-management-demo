CREATE TABLE employees (
       personal_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
       name            VARCHAR(255) NOT NULL,
       team            VARCHAR(255),
       team_lead_id    BIGINT,
       CONSTRAINT fk_team_lead FOREIGN KEY (team_lead_id) REFERENCES employees (personal_id)
);