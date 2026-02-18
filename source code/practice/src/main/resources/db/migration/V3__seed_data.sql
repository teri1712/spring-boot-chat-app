-- Themes
INSERT INTO theme (id, background)
VALUES (1, 'https://i.pinimg.com/736x/7d/ec/62/7dec625b5a1bb168d8f472e75982cb88.jpg'),
       (2, 'https://i.pinimg.com/1200x/d3/1b/9d/d31b9dcca8abe587c39ce24c886961b6.jpg'),
       (3, 'https://i.pinimg.com/736x/4c/02/62/4c0262c1337e81543eb05e4cdaad71a9.jpg'),
       (4, 'https://i.pinimg.com/1200x/d5/76/e3/d576e32afeee903278a0c94858b31371.jpg'),
       (5, 'https://i.pinimg.com/1200x/1b/fb/64/1bfb64e8bf12b474abc8afa10eb600f0.jpg');

-- users (as provided)
INSERT INTO user_member (id, username, password, name, gender, dob, role, version, avatar)
VALUES ('00000000-0000-0000-0000-000000000002', 'Luffy', '$2y$10$JknJ3L/C9cTH1y2Uyu1DY.LxqHHsLu6ZtyZFm5Bpav3DurKwOmHm2',
        'Luffy', 1.0, CURRENT_TIMESTAMP, 'ROLE_USER', 0,
        'https://i.pinimg.com/736x/40/31/00/403100c729d0ef4551aeadfa57d9cbf7.jpg');

INSERT INTO user_member (id, username, password, name, gender, dob, role, version, avatar)
VALUES ('00000000-0000-0000-0000-000000000003', 'Nami', '$2y$10$XevrAA3o1R2p2kpjOXO7BeFQgmGCLXUcvIyZWp3eyl9BjsaJgjTHK',
        'Nami', 1.0, CURRENT_TIMESTAMP, 'ROLE_USER', 0,
        'https://i.pinimg.com/736x/74/d2/0f/74d20f3bdbeeaea11da2bf70cd1ef60a.jpg');

INSERT INTO user_member (id, username, password, name, gender, dob, role, version, avatar)
VALUES ('00000000-0000-0000-0000-000000000004', 'Chopper',
        '$2y$10$kZBoOZpP9PRmjJNFqNB1b.Hokw66GviLP0IZ4TruPBl.W.OBIvXFa', 'Chopper', 1.0, CURRENT_TIMESTAMP, 'ROLE_USER',
        0,
        'https://i.pinimg.com/736x/ec/a7/0f/eca70f7274805de4be9553a9632778bf.jpg');

INSERT INTO user_member (id, username, password, name, dob, gender, role, version, avatar)
VALUES ('00000000-0000-0000-0000-000000000005', 'Zoro', '$2y$10$S298DZN6DkqbjbBaP9MSYuE0ckWbCj3HyLViP.sP/zaqnoUUT6VUK',
        'Zoro', CURRENT_TIMESTAMP, 1.0, 'ROLE_USER', 0,
        'https://i.pinimg.com/736x/79/e2/c9/79e2c9402014ead1eebf6c9f184c5bf8.jpg');

-- a chat
INSERT INTO chat (identifier,
                  version,
                  interaction_count,
                  first_creator,
                  second_creator,
                  icon_id,
                  room_name,
                  room_avatar,
                  theme,
                  max_participants)
VALUES ('chat-strawhat',
        0,
        0,
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000003',
        1,
        'Straw Hat Crew',
        'https://example.com/strawhat.jpg',
        '1',
        10);

-- participants
INSERT INTO participant (join_date, version, user_id, chat_id, write, read)
VALUES (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000002', 'chat-strawhat', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000003', 'chat-strawhat', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000004', 'chat-strawhat', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000005', 'chat-strawhat', true, true);

-- chat events (text)
INSERT INTO chat_event (id,
                        sender_id,
                        event_type,
                        owner_id,
                        version,
                        chat_id,
                        event_version,
                        created_at,
                        icon_id,
                        content)
VALUES (gen_random_uuid(),
        '00000000-0000-0000-0000-000000000002',
        'TEXT',
        '00000000-0000-0000-0000-000000000002',
        0,
        'chat-strawhat',
        1,
        CURRENT_TIMESTAMP,
        1,
        'I will become King of the Pirates!'),
       (gen_random_uuid(),
        '00000000-0000-0000-0000-000000000003',
        'TEXT',
        '00000000-0000-0000-0000-000000000003',
        0,
        'chat-strawhat',
        2,
        CURRENT_TIMESTAMP,
        1,
        'Focus on the treasure first, captain.');

-- per-user chat history
INSERT INTO chat_history (room_name,
                          room_avatar,
                          version,
                          messages,
                          seen_by,
                          modified_at,
                          chat_id,
                          owner_id,
                          value)
VALUES ('Straw Hat Crew',
        'https://example.com/strawhat.jpg',
        0,
        '[]'::jsonb,
        '[]'::jsonb,
        CURRENT_TIMESTAMP,
        'chat-strawhat',
        '00000000-0000-0000-0000-000000000002',
        0),
       ('Straw Hat Crew',
        'https://example.com/strawhat.jpg',
        0,
        '[]'::jsonb,
        '[]'::jsonb,
        CURRENT_TIMESTAMP,
        'chat-strawhat',
        '00000000-0000-0000-0000-000000000003',
        0),
       ('Straw Hat Crew',
        'https://example.com/strawhat.jpg',
        0,
        '[]'::jsonb,
        '[]'::jsonb,
        CURRENT_TIMESTAMP,
        'chat-strawhat',
        '00000000-0000-0000-0000-000000000004',
        0),
       ('Straw Hat Crew',
        'https://example.com/strawhat.jpg',
        0,
        '[]'::jsonb,
        '[]'::jsonb,
        CURRENT_TIMESTAMP,
        'chat-strawhat',
        '00000000-0000-0000-0000-000000000005',
        0);


-- Private chats (identifier = smaller_uuid + '+' + bigger_uuid)
INSERT INTO chat (identifier, version, interaction_count, first_creator, second_creator, icon_id, room_name,
                  room_avatar, theme, max_participants)
VALUES ('00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003', 0, 0,
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', NULL, 'Direct chat', NULL, '1',
        2),
       ('00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000004', 0, 0,
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004', NULL, 'Direct chat', NULL, '1',
        2),
       ('00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000005', 0, 0,
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000005', NULL, 'Direct chat', NULL, '1',
        2),
       ('00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000004', 0, 0,
        '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000004', NULL, 'Direct chat', NULL, '1',
        2),
       ('00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000005', 0, 0,
        '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000005', NULL, 'Direct chat', NULL, '1',
        2),
       ('00000000-0000-0000-0000-000000000004+00000000-0000-0000-0000-000000000005', 0, 0,
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000005', NULL, 'Direct chat', NULL, '1',
        2);

-- Participants for each private chat (both users)
INSERT INTO participant (join_date, version, user_id, chat_id, write, read)
VALUES (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003', true, true),

       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000004', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000004', true, true),

       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000005', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000005', true, true),

       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000004', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000004', true, true),

       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000005', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000005', true, true),

       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000004+00000000-0000-0000-0000-000000000005', true, true),
       (CURRENT_TIMESTAMP, 0, '00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000004+00000000-0000-0000-0000-000000000005', true, true);

-- One sample text event per private chat (sent by the smaller UUID user)
INSERT INTO chat_event (id, sender_id, event_type, owner_id, version, chat_id, event_version, created_at, icon_id,
                        content)
VALUES (gen_random_uuid(), '00000000-0000-0000-0000-000000000002', 'TEXT', '00000000-0000-0000-0000-000000000002', 0,
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003', 1, CURRENT_TIMESTAMP, NULL,
        'Hey — this is a direct chat.'),
       (gen_random_uuid(), '00000000-0000-0000-0000-000000000002', 'TEXT', '00000000-0000-0000-0000-000000000002', 0,
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000004', 1, CURRENT_TIMESTAMP, NULL,
        'Hello!'),
       (gen_random_uuid(), '00000000-0000-0000-0000-000000000002', 'TEXT', '00000000-0000-0000-0000-000000000002', 0,
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000005', 1, CURRENT_TIMESTAMP, NULL,
        'Direct message initiated.'),
       (gen_random_uuid(), '00000000-0000-0000-0000-000000000003', 'TEXT', '00000000-0000-0000-0000-000000000003', 0,
        '00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000004', 1, CURRENT_TIMESTAMP, NULL,
        'Hi there.'),
       (gen_random_uuid(), '00000000-0000-0000-0000-000000000003', 'TEXT', '00000000-0000-0000-0000-000000000003', 0,
        '00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000005', 1, CURRENT_TIMESTAMP, NULL,
        'Hello!'),
       (gen_random_uuid(), '00000000-0000-0000-0000-000000000004', 'TEXT', '00000000-0000-0000-0000-000000000004', 0,
        '00000000-0000-0000-0000-000000000004+00000000-0000-0000-0000-000000000005', 1, CURRENT_TIMESTAMP, NULL,
        'Started a private chat.');

-- Per-user empty chat_history entries for each private chat
INSERT INTO chat_history (room_name, room_avatar, version, messages, seen_by, modified_at, chat_id, owner_id, value)
VALUES ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000002', 0),
       ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000003', 0),

       ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000002', 0),
       ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000004', 0),

       ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000002', 0),
       ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000002+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000005', 0),

       ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000003', 0),
       ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000004', 0),

       ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000003', 0),
       ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000003+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000005', 0),

       ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000004+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000004', 0),
       ('Direct chat', NULL, 0, '[]'::jsonb, '[]'::jsonb, CURRENT_TIMESTAMP,
        '00000000-0000-0000-0000-000000000004+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000005', 0);