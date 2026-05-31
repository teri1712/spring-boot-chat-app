create index idx_owner_room_sequence on inbox_log (owner_id, conversation_id, sequence_id);

create index idx_converastion_owner_room on conversation (owner_id, room_id);;

drop index idx_participant_chat;
drop index idx_conversation_hash;

create index idx_conversation_hash on conversation (owner_id, hash_value);
