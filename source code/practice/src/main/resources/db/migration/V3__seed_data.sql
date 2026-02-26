INSERT INTO theme (id, background)
VALUES (nextval('theme_seq'), 'https://i.pinimg.com/736x/7d/ec/62/7dec625b5a1bb168d8f472e75982cb88.jpg'),
       (nextval('theme_seq'), 'https://i.pinimg.com/1200x/d3/1b/9d/d31b9dcca8abe587c39ce24c886961b6.jpg'),
       (nextval('theme_seq'), 'https://i.pinimg.com/736x/4c/02/62/4c0262c1337e81543eb05e4cdaad71a9.jpg'),
       (nextval('theme_seq'), 'https://i.pinimg.com/1200x/d5/76/e3/d576e32afeee903278a0c94858b31371.jpg'),
       (nextval('theme_seq'), 'https://i.pinimg.com/1200x/1b/fb/64/1bfb64e8bf12b474abc8afa10eb600f0.jpg');

INSERT INTO user_member (id, username, password, name, gender, dob, role, version, avatar)
VALUES ('00000000-0000-0000-0000-000000000002', 'Luffy',
        '$2y$10$JknJ3L/C9cTH1y2Uyu1DY.LxqHHsLu6ZtyZFm5Bpav3DurKwOmHm2',
        'Luffy', 1.0, CURRENT_TIMESTAMP, 'ROLE_USER', 0,
        'https://i.pinimg.com/736x/40/31/00/403100c729d0ef4551aeadfa57d9cbf7.jpg'),

       ('00000000-0000-0000-0000-000000000003', 'Nami',
        '$2y$10$XevrAA3o1R2p2kpjOXO7BeFQgmGCLXUcvIyZWp3eyl9BjsaJgjTHK',
        'Nami', 1.0, CURRENT_TIMESTAMP, 'ROLE_USER', 0,
        'https://i.pinimg.com/736x/74/d2/0f/74d20f3bdbeeaea11da2bf70cd1ef60a.jpg'),

       ('00000000-0000-0000-0000-000000000004', 'Chopper',
        '$2y$10$kZBoOZpP9PRmjJNFqNB1b.Hokw66GviLP0IZ4TruPBl.W.OBIvXFa',
        'Chopper', 1.0, CURRENT_TIMESTAMP, 'ROLE_USER', 0,
        'https://i.pinimg.com/736x/ec/a7/0f/eca70f7274805de4be9553a9632778bf.jpg'),

       ('00000000-0000-0000-0000-000000000005', 'Zoro',
        '$2y$10$S298DZN6DkqbjbBaP9MSYuE0ckWbCj3HyLViP.sP/zaqnoUUT6VUK',
        'Zoro', 1.0, CURRENT_TIMESTAMP, 'ROLE_USER', 0,
        'https://i.pinimg.com/736x/79/e2/c9/79e2c9402014ead1eebf6c9f184c5bf8.jpg');


-- Private chat: Luffy & Nami
INSERT INTO chat (identifier, event_version, caller_id, partner_id, icon_id,
                  room_name, room_avatar, theme, max_participants)
VALUES ('00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003', 0,
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000003',
        1, 'Luffy & Nami', NULL, '1', 2);

-- Group chat
INSERT INTO chat (identifier, event_version, icon_id,
                  room_name, theme, max_participants)
VALUES ('chat_group_1', 0,
        2, 'Straw Hat Crew', '2', 10);


-- Private chat participants
INSERT INTO participant (join_date, version, user_id, chat_id, write, read)
VALUES (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003', true, true);

-- Group chat participants
INSERT INTO participant (join_date, version, user_id, chat_id, write, read)
VALUES (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000002', 'chat_group_1', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000003', 'chat_group_1', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000004', 'chat_group_1', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000005', 'chat_group_1', true, true);



INSERT INTO message (sequence_id, chat_event_id, sender_id, message_type,
                     chat_id, created_at, updated_at,
                     icon_id, room_name, theme, content)
VALUES (nextval('message_seq'),
        gen_random_uuid(),
        '00000000-0000-0000-0000-000000000002',
        'TEXT',
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        1,
        'Luffy & Nami',
        '1',
        'Nami! Let’s find the One Piece!');

INSERT INTO message (sequence_id, chat_event_id, sender_id, message_type,
                     chat_id, created_at, updated_at,
                     icon_id, room_name, theme,
                     uri, width, height, format)
VALUES (nextval('message_seq'),
        gen_random_uuid(),
        '00000000-0000-0000-0000-000000000003',
        'IMAGE',
        'chat_group_1',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        2,
        'Straw Hat Crew',
        '2',
        'https://example.com/sunny.jpg',
        800,
        600,
        'jpg');


INSERT INTO message (sequence_id, chat_event_id, sender_id, message_type,
                     chat_id, created_at, updated_at,
                     icon_id, room_name, theme,
                     filename, uri, size)
VALUES (nextval('message_seq'),
        gen_random_uuid(),
        '00000000-0000-0000-0000-000000000005',
        'FILE',
        'chat_group_1',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        2,
        'Straw Hat Crew',
        '2',
        'map.pdf',
        'https://example.com/map.pdf',
        2048);


INSERT INTO seen_pointer (id, sender_id, chat_id, at, message_id)
VALUES (nextval('seen_pointer_seq'), '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP, 1),
       (nextval('seen_pointer_seq'), '00000000-0000-0000-0000-000000000002', 'chat_group_1', CURRENT_TIMESTAMP, 51);



INSERT INTO inbox_log (sequence_id, chat_id, sender_id, owner_id,
                       message_id, action, message_state)
VALUES (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000002',
        1,
        'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Nami! Let’s find the One Piece!",
          "seenBy": [
            "00000000-0000-0000-0000-000000000002"
          ]
        }'::jsonb),

       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000003',
        1,
        'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Nami! Let’s find the One Piece!",
          "seenBy": []
        }'::jsonb);


INSERT INTO inbox_log (sequence_id, chat_id, sender_id, owner_id,
                       message_id, action, message_state)
VALUES (nextval('inbox_log_seq'), 'chat_group_1', '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000002',
        51, 'DELIVERED',
        '{
          "type": "IMAGE",
          "uri": "https://example.com/sunny.jpg",
          "width": 800,
          "height": 600,
          "seenBy": []
        }'::jsonb),

       (nextval('inbox_log_seq'), 'chat_group_1', '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000003',
        51, 'DELIVERED',
        '{
          "type": "IMAGE",
          "uri": "https://example.com/sunny.jpg",
          "width": 800,
          "height": 600,
          "seenBy": [
            "00000000-0000-0000-0000-000000000003"
          ]
        }'::jsonb),

       (nextval('inbox_log_seq'), 'chat_group_1', '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000004',
        51, 'DELIVERED',
        '{
          "type": "IMAGE",
          "uri": "https://example.com/sunny.jpg",
          "width": 800,
          "height": 600,
          "seenBy": []
        }'::jsonb),

       (nextval('inbox_log_seq'), 'chat_group_1', '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000005',
        51, 'DELIVERED',
        '{
          "type": "IMAGE",
          "uri": "https://example.com/sunny.jpg",
          "width": 800,
          "height": 600,
          "seenBy": []
        }'::jsonb);


INSERT INTO inbox_log (sequence_id, chat_id, sender_id, owner_id,
                       message_id, action, message_state)
VALUES (nextval('inbox_log_seq'), 'chat_group_1', '00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000002',
        101, 'DELIVERED',
        '{
          "type": "FILE",
          "filename": "map.pdf",
          "size": 2048,
          "seenBy": []
        }'::jsonb),

       (nextval('inbox_log_seq'), 'chat_group_1', '00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000003',
        101, 'DELIVERED',
        '{
          "type": "FILE",
          "filename": "map.pdf",
          "size": 2048,
          "seenBy": []
        }'::jsonb),

       (nextval('inbox_log_seq'), 'chat_group_1', '00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000004',
        101, 'DELIVERED',
        '{
          "type": "FILE",
          "filename": "map.pdf",
          "size": 2048,
          "seenBy": []
        }'::jsonb),

       (nextval('inbox_log_seq'), 'chat_group_1', '00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000005',
        101, 'DELIVERED',
        '{
          "type": "FILE",
          "filename": "map.pdf",
          "size": 2048,
          "seenBy": [
            "00000000-0000-0000-0000-000000000005"
          ]
        }'::jsonb);