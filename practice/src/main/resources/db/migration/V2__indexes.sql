CREATE INDEX idx_user_name ON user_member (name);

CREATE INDEX idx_chat_event_look_up ON chat_event (event_version, owner_id, first_user, second_user);
