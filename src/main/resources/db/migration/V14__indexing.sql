create index idx_room_chat on room (chat_id);
create index idx_owner_room on conversation (owner_id, modified_at);
create index idx_conversation_hash on conversation (hash_value);


create index idx_log_owner_chat on inbox_log (owner_id, chat_id, sequence_id);
create index idx_log_owner on inbox_log (owner_id, sequence_id);


create index idx_message_chat on message (chat_id, sequence_id);

create index idx_seen_pointer on seen_pointer (chat_id, sender_id);


create index idx_participant_chat on participant (chat_id);

create index idx_setting_chat on setting (identifier);

create index idx_user_username on user_member (username);
