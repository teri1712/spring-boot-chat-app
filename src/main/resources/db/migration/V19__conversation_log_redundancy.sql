ALTER TABLE inbox_log
    ADD column conversation_id BIGINT;

ALTER TABLE conversation
    ADD column room_id BIGINT;