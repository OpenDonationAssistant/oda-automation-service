CREATE TABLE IF NOT EXISTS alert_data (
    id VARCHAR(255) PRIMARY KEY,
    recipient_id VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    message TEXT,
    amount TEXT
);

CREATE TABLE IF NOT EXISTS alert_link (
    id VARCHAR(255) PRIMARY KEY,
    alert_id VARCHAR(255) NOT NULL,
    origin_id VARCHAR(255) NOT NULL,
    source VARCHAR(255) NOT NULL
);
