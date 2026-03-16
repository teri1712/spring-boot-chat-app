alter table conversation
    drop column name;
alter table conversation
    drop column avatar;

alter table chat
    drop column partner_id;
alter table chat
    add column partners jsonb;


create table room
(
    chat_id           varchar(255) not null primary key,
    name              varchar(255),
    avatar            varchar(255),
    creator           UUID         not null,
    version           int,
    representatives   jsonb        not null,
    participant_count int
);

alter table room
    add constraint fk_room_creator foreign key (creator) references user_member (id);
