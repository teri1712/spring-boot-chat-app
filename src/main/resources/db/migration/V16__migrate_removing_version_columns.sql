create sequence conversation_seq start with 1 increment by 50;

alter table conversation
    drop column version;
alter table conversation
    drop constraint pk_conversation;
alter table conversation
    add column id bigint;

update conversation
set id = nextval('conversation_seq');

alter table conversation
    add constraint pk_conversation primary key (id);

create index idx_conversation_chat_id_owner_id on conversation (chat_id, owner_id);

SELECT setval(
               'conversation_seq',
               (SELECT MAX(id) FROM conversation)
       );


create sequence room_seq start with 1 increment by 50;

alter table room
    drop column version;
alter table room
    drop constraint room_pkey;
alter table room
    add column id bigint;

update room
set id = nextval('room_seq');

alter table room
    add constraint pk_room primary key (id);


SELECT setval(
               'room_seq',
               (SELECT MAX(id) FROM room)
       );

create index idx_room_chat_id on room (chat_id);
