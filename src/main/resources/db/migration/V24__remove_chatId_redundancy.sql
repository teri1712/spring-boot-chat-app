update inbox_log
set conversation_id = (select conversation.id
                       from conversation
                       where conversation.owner_id = inbox_log.owner_id
                         and inbox_log.chat_id = conversation.chat_id);
update conversation
set room_id = (select id from room where room.chat_id = conversation.chat_id);

alter table conversation
    drop column chat_id;
alter table inbox_log
    drop column chat_id;
