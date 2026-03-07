ALTER TABLE chat
    DROP COLUMN room_name;

ALTER TABLE chat
    DROP COLUMN room_avatar;

ALTER TABLE chat
    DROP COLUMN theme;

ALTER TABLE chat
    DROP COLUMN icon_id;

ALTER TABLE chat
    DROP COLUMN event_version;

ALTER TABLE chat
    ADD COLUMN version int not null default 0;

alter table chat
    drop column last_activity;

alter table chat
    rename column identifier to chat_id;

create table setting
(
    id            bigint primary key,
    identifier    varchar(255) not null,
    icon_id       int,
    room_name     varchar(255),
    room_avatar   varchar(255),
    theme_id      bigint,
    last_activity timestamptz
);

alter table setting
    add constraint fk_settings_theme foreign key (theme_id) references theme (id);

alter table setting
    add constraint fk_settings_chat foreign key (identifier) references chat (chat_id);


CREATE SEQUENCE IF NOT EXISTS setting_seq START WITH 1 INCREMENT BY 50;