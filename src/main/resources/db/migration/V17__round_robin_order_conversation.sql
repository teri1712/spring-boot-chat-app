alter table conversation
    add column round_robin int default 0;
alter table conversation
    add column participant_index int default 0;