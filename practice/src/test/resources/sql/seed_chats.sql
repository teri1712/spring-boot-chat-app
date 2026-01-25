-- Alice (1111...) and Bob (2222...)
-- Alice (1111...) and Charlie (3333...)

INSERT INTO chat (first_user, second_user, resource_id, room_name, interact_time, message_count)
VALUES ('11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 1, 'Room alice and bob', NOW(),
        0),
       ('11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 1, 'Room alice and charlie',
        NOW(), 0);

-- Alice's chats
INSERT INTO chat_order (chat_first_user, chat_second_user, owner_id, current_version)
VALUES ('11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222',
        '11111111-1111-1111-1111-111111111111', 1),
       ('11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333',
        '11111111-1111-1111-1111-111111111111', 0);
UPDATE sync_context
SET event_version = 1
WHERE owner_id = '11111111-1111-1111-1111-111111111111';


-- Bob's chat
INSERT INTO chat_order (chat_first_user, chat_second_user, owner_id, current_version)
VALUES ('11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222',
        '22222222-2222-2222-2222-222222222222', 0);

-- Charlie's chat
INSERT INTO chat_order (chat_first_user, chat_second_user, owner_id, current_version)
VALUES ('11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333',
        '33333333-3333-3333-3333-333333333333', 0);
