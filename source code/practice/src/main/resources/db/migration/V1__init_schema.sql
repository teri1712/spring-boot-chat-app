CREATE SEQUENCE IF NOT EXISTS theme_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE chat
(
    identifier        VARCHAR(255) NOT NULL,
    version           INTEGER,
    interaction_count INTEGER,
    first_creator     UUID,
    second_creator    UUID,
    icon_id           INTEGER,
    room_name         VARCHAR(255),
    room_avatar       VARCHAR(255),
    theme             VARCHAR(255),
    max_participants  INTEGER,
    CONSTRAINT pk_chat PRIMARY KEY (identifier)
);

CREATE TABLE chat_event
(
    id                   UUID         NOT NULL,
    sender_id            UUID         NOT NULL,
    event_type           VARCHAR(255),
    owner_id             UUID         NOT NULL,
    version              INTEGER,
    chat_id              VARCHAR(255) NOT NULL,
    room_name_snapshot   VARCHAR(255),
    room_avatar_snapshot VARCHAR(255),
    event_version        INTEGER,
    created_at           TIMESTAMPTZ,
    icon_id              INTEGER,
    room_name            VARCHAR(255),
    room_avatar          VARCHAR(255),
    theme                VARCHAR(255),
    filename             VARCHAR(255),
    uri                  VARCHAR(255),
    size                 INTEGER,
    content              VARCHAR(255),
    at                   TIMESTAMPTZ,
    width                INTEGER,
    height               INTEGER,
    format               VARCHAR(255),
    CONSTRAINT pk_chatevent PRIMARY KEY (id)
);

CREATE TABLE chat_history
(
    room_name   VARCHAR(255),
    room_avatar VARCHAR(255),
    version     INTEGER,
    messages    JSONB,
    seen_by     JSONB,
    modified_at TIMESTAMPTZ,
    chat_id     VARCHAR(255) NOT NULL,
    owner_id    UUID         NOT NULL,
    value       BIGINT,
    CONSTRAINT pk_chathistory PRIMARY KEY (chat_id, owner_id)
);

CREATE TABLE file_receipt
(
    idempotent_key UUID NOT NULL,
    chat_id        VARCHAR(255),
    sender_id      UUID,
    created_at     TIMESTAMPTZ,
    version        INTEGER,
    uri            VARCHAR(255),
    filename       VARCHAR(255),
    size           INTEGER,
    CONSTRAINT pk_filereceipt PRIMARY KEY (idempotent_key)
);

CREATE TABLE icon_receipt
(
    idempotent_key UUID NOT NULL,
    chat_id        VARCHAR(255),
    sender_id      UUID,
    created_at     TIMESTAMPTZ,
    version        INTEGER,
    icon_id        INTEGER,
    CONSTRAINT pk_iconreceipt PRIMARY KEY (idempotent_key)
);

CREATE TABLE image_receipt
(
    idempotent_key UUID NOT NULL,
    chat_id        VARCHAR(255),
    sender_id      UUID,
    created_at     TIMESTAMPTZ,
    version        INTEGER,
    uri            VARCHAR(255),
    width          INTEGER,
    height         INTEGER,
    filename       VARCHAR(255),
    format         VARCHAR(255),
    CONSTRAINT pk_imagereceipt PRIMARY KEY (idempotent_key)
);

CREATE TABLE participant
(
    join_date TIMESTAMPTZ  NOT NULL,
    version   INTEGER,
    user_id   UUID         NOT NULL,
    chat_id   VARCHAR(255) NOT NULL,
    write     BOOLEAN      NOT NULL,
    read      BOOLEAN      NOT NULL,
    CONSTRAINT pk_participant PRIMARY KEY (user_id, chat_id)
);

CREATE TABLE preference_receipt
(
    idempotent_key UUID NOT NULL,
    chat_id        VARCHAR(255),
    sender_id      UUID,
    created_at     TIMESTAMPTZ,
    version        INTEGER,
    icon_id        INTEGER,
    room_name      VARCHAR(255),
    room_avatar    VARCHAR(255),
    theme_id       BIGINT,
    CONSTRAINT pk_preferencereceipt PRIMARY KEY (idempotent_key)
);

CREATE TABLE seen_receipt
(
    idempotent_key UUID NOT NULL,
    chat_id        VARCHAR(255),
    sender_id      UUID,
    created_at     TIMESTAMPTZ,
    version        INTEGER,
    at             TIMESTAMPTZ,
    CONSTRAINT pk_seenreceipt PRIMARY KEY (idempotent_key)
);

CREATE TABLE text_receipt
(
    idempotent_key UUID NOT NULL,
    chat_id        VARCHAR(255),
    sender_id      UUID,
    created_at     TIMESTAMPTZ,
    version        INTEGER,
    content        VARCHAR(255),
    CONSTRAINT pk_textreceipt PRIMARY KEY (idempotent_key)
);

CREATE TABLE theme
(
    id         BIGINT NOT NULL,
    background VARCHAR(255),
    CONSTRAINT pk_theme PRIMARY KEY (id)
);

CREATE TABLE user_member
(
    id       UUID         NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name     VARCHAR(255),
    dob      TIMESTAMPTZ,
    role     VARCHAR(255),
    avatar   VARCHAR(255),
    version  INTEGER,
    gender   FLOAT        NOT NULL,
    CONSTRAINT pk_user_member PRIMARY KEY (id)
);

CREATE TABLE user_thread
(
    user_id       UUID NOT NULL,
    version       INTEGER,
    event_version INTEGER,
    event_id      UUID,
    CONSTRAINT pk_userthread PRIMARY KEY (user_id)
);

ALTER TABLE user_member
    ADD CONSTRAINT uc_user_member_username UNIQUE (username);