CREATE TABLE IF NOT EXISTS wordblacklist (
    id UUID PRIMARY KEY,
    recipient_id varchar(255) NOT NULL,
    words text
);
