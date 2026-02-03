CREATE INDEX idx_user_name ON user_member USING HASH (username);

CREATE INDEX idx_chat_event_look_up ON chat_event (first_user, second_user, owner_id, event_version);

CREATE INDEX idx_user_event_look_up ON chat_event (owner_id, event_version);

CREATE INDEX idx_chat_order_listing ON chat_order (owner_id, current_version);

CREATE UNIQUE INDEX idx_chat_order_owner ON chat_order (owner_id, chat_first_user, chat_second_user);