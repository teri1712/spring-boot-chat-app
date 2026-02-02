CREATE TYPE outbox_status AS ENUM ('PENDING', 'SENT');
CREATE SEQUENCE IF NOT EXISTS outbox_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE outbox
(
    id         BIGINT        NOT NULL PRIMARY KEY,
    topic      VARCHAR(255)  NOT NULL,
    key        VARCHAR(255)  NOT NULL,
    payload    JSONB         NOT NULL,
    created_at TIMESTAMPTZ   NOT NULL,
    status     outbox_status NOT NULL
);


CREATE INDEX outbox_created_at_idx ON outbox (status, created_at) INCLUDE (id, topic, key, payload);