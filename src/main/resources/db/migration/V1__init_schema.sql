CREATE SEQUENCE IF NOT EXISTS inbox_log_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS message_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS seen_pointer_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS theme_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE chat
(
    identifier       VARCHAR(255) NOT NULL,
    event_version    INTEGER,
    caller_id        UUID,
    partner_id       UUID,
    icon_id          INTEGER,
    room_name        VARCHAR(255),
    room_avatar      VARCHAR(255),
    theme            VARCHAR(255),
    max_participants INTEGER,
    CONSTRAINT pk_chat PRIMARY KEY (identifier)
);

CREATE TABLE conversation
(
    name        VARCHAR(255),
    avatar      VARCHAR(255),
    version     INTEGER,
    recents     JSONB,
    modified_at TIMESTAMPTZ,
    chat_id     VARCHAR(255) NOT NULL,
    owner_id    UUID         NOT NULL,
    hash_value  BIGINT,
    CONSTRAINT pk_conversation PRIMARY KEY (chat_id, owner_id)
);

CREATE TABLE file_room_event
(
    id            UUID NOT NULL,
    chat_id       VARCHAR(255),
    sender_id     UUID,
    created_at    TIMESTAMPTZ,
    version       INTEGER,
    event_version INTEGER,
    uri           VARCHAR(255),
    filename      VARCHAR(255),
    size          INTEGER,
    CONSTRAINT pk_filechatevent PRIMARY KEY (id)
);

CREATE TABLE icon_room_event
(
    id            UUID NOT NULL,
    chat_id       VARCHAR(255),
    sender_id     UUID,
    created_at    TIMESTAMPTZ,
    version       INTEGER,
    event_version INTEGER,
    icon_id       INTEGER,
    CONSTRAINT pk_iconchatevent PRIMARY KEY (id)
);

CREATE TABLE image_room_event
(
    id            UUID NOT NULL,
    chat_id       VARCHAR(255),
    sender_id     UUID,
    created_at    TIMESTAMPTZ,
    version       INTEGER,
    event_version INTEGER,
    uri           VARCHAR(255),
    width         INTEGER,
    height        INTEGER,
    filename      VARCHAR(255),
    format        VARCHAR(255),
    CONSTRAINT pk_imagechatevent PRIMARY KEY (id)
);

CREATE TABLE inbox_log
(
    sequence_id   BIGINT NOT NULL,
    chat_id       VARCHAR(255),
    sender_id     UUID,
    owner_id      UUID,
    message_id    BIGINT,
    action        VARCHAR(255),
    message_state JSONB,
    CONSTRAINT pk_inboxlog PRIMARY KEY (sequence_id)
);

CREATE TABLE message
(
    sequence_id   BIGINT       NOT NULL,
    chat_event_id UUID,
    sender_id     UUID         NOT NULL,
    message_type  VARCHAR(255),
    chat_id       VARCHAR(255) NOT NULL,
    created_at    TIMESTAMPTZ,
    updated_at    TIMESTAMPTZ,
    filename      VARCHAR(255),
    uri           VARCHAR(255),
    size          INTEGER,
    icon_id       INTEGER,
    room_name     VARCHAR(255),
    room_avatar   VARCHAR(255),
    theme         VARCHAR(255),
    content       VARCHAR(255),
    width         INTEGER,
    height        INTEGER,
    format        VARCHAR(255),
    CONSTRAINT pk_message PRIMARY KEY (sequence_id)
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

CREATE TABLE preference_room_event
(
    id            UUID NOT NULL,
    chat_id       VARCHAR(255),
    sender_id     UUID,
    created_at    TIMESTAMPTZ,
    version       INTEGER,
    event_version INTEGER,
    icon_id       INTEGER,
    name          VARCHAR(255),
    avatar        VARCHAR(255),
    theme         VARCHAR(255),
    CONSTRAINT pk_preferencechatevent PRIMARY KEY (id)
);

CREATE TABLE seen_room_event
(
    id            UUID NOT NULL,
    chat_id       VARCHAR(255),
    sender_id     UUID,
    created_at    TIMESTAMPTZ,
    version       INTEGER,
    event_version INTEGER,
    at            TIMESTAMPTZ,
    CONSTRAINT pk_seenchatevent PRIMARY KEY (id)
);

CREATE TABLE seen_pointer
(
    id         BIGINT NOT NULL,
    sender_id  UUID,
    chat_id    VARCHAR(255),
    at         TIMESTAMPTZ,
    message_id BIGINT,
    CONSTRAINT pk_seenpointer PRIMARY KEY (id)
);

CREATE TABLE text_room_event
(
    id            UUID NOT NULL,
    chat_id       VARCHAR(255),
    sender_id     UUID,
    created_at    TIMESTAMPTZ,
    version       INTEGER,
    event_version INTEGER,
    content       VARCHAR(255),
    CONSTRAINT pk_textchatevent PRIMARY KEY (id)
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

ALTER TABLE user_member
    ADD CONSTRAINT uc_user_member_username UNIQUE (username);

ALTER TABLE seen_pointer
    ADD CONSTRAINT FK_SEENPOINTER_ON_MESSAGE FOREIGN KEY (message_id) REFERENCES message (sequence_id);