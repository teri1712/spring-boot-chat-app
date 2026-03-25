update conversation
set recents = (select jsonb_agg(jsonb_set(recent, '{createdAt}', to_jsonb(NOW())))
               from jsonb_array_elements(recents) recent);


alter table setting
    rename column room_name to custom_name;
alter table setting
    rename column room_avatar to custom_avatar;

alter table room
    rename column name to custom_name;
alter table room
    rename column avatar to custom_avatar;

alter table room
    add column last_activity timestamptz default now();
alter table setting
    drop column last_activity;


alter table room
    alter column version set default 1;

SELECT setval(
               'inbox_log_seq',
               (SELECT MAX(sequence_id) FROM inbox_log)
       );

SELECT setval(
               'message_seq',
               (SELECT MAX(sequence_id) FROM message)
       );