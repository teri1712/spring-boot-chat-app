-- Admin
INSERT INTO user_member (id, username, password, name, dob, role, version, width, height)
VALUES ('00000000-0000-0000-0000-000000000001', 'admin', '$2y$10$078diduQ1UJ7lobYk4flDurh0NHB0JO3R34R71x4mVLdAhs8cezvG',
        'admin', CURRENT_TIMESTAMP, 'ROLE_ADMIN', 0, 512,
        512);

INSERT INTO sync_context (owner_id, event_version)
VALUES ('00000000-0000-0000-0000-000000000001', 0);

-- Themes
INSERT INTO theme (id, uri, width, height, format)
VALUES (1, 'https://i.pinimg.com/736x/7d/ec/62/7dec625b5a1bb168d8f472e75982cb88.jpg', 512, 512, 'jpg'),
       (2, 'https://i.pinimg.com/1200x/d3/1b/9d/d31b9dcca8abe587c39ce24c886961b6.jpg', 512, 512, 'jpg'),
       (3, 'https://i.pinimg.com/736x/4c/02/62/4c0262c1337e81543eb05e4cdaad71a9.jpg', 512, 512, 'jpg'),
       (4, 'https://i.pinimg.com/1200x/d5/76/e3/d576e32afeee903278a0c94858b31371.jpg', 512, 512, 'jpg'),
       (5, 'https://i.pinimg.com/1200x/1b/fb/64/1bfb64e8bf12b474abc8afa10eb600f0.jpg', 512, 512, 'jpg');

-- Users
-- Luffy
INSERT INTO user_member (id, username, password, name, gender, dob, role, version, uri, format, width, height)
VALUES ('00000000-0000-0000-0000-000000000002', 'Luffy', '$2y$10$JknJ3L/C9cTH1y2Uyu1DY.LxqHHsLu6ZtyZFm5Bpav3DurKwOmHm2',
        'Luffy', 1.0, CURRENT_TIMESTAMP, 'ROLE_USER', 0,
        'https://i.pinimg.com/736x/40/31/00/403100c729d0ef4551aeadfa57d9cbf7.jpg', 'jpeg',
        512, 512);
INSERT INTO sync_context (owner_id, event_version)
VALUES ('00000000-0000-0000-0000-000000000002', 0);

-- Nami
INSERT INTO user_member (id, username, password, name, gender, dob, role, version, uri, format, width, height)
VALUES ('00000000-0000-0000-0000-000000000003', 'Nami', '$2y$10$XevrAA3o1R2p2kpjOXO7BeFQgmGCLXUcvIyZWp3eyl9BjsaJgjTHK',
        'Nami', 1.0, CURRENT_TIMESTAMP, 'ROLE_USER', 0,
        'https://i.pinimg.com/736x/74/d2/0f/74d20f3bdbeeaea11da2bf70cd1ef60a.jpg', 'jpeg', 512,
        512);
INSERT INTO sync_context (owner_id, event_version)
VALUES ('00000000-0000-0000-0000-000000000003', 0);

-- Chopper
INSERT INTO user_member (id, username, password, name, gender, dob, role, version, uri, format, width, height)
VALUES ('00000000-0000-0000-0000-000000000004', 'Chopper',
        '$2y$10$kZBoOZpP9PRmjJNFqNB1b.Hokw66GviLP0IZ4TruPBl.W.OBIvXFa', 'Chopper', 1.0, CURRENT_TIMESTAMP, 'ROLE_USER',
        0,
        'https://i.pinimg.com/736x/ec/a7/0f/eca70f7274805de4be9553a9632778bf.jpg', 'jpeg', 512, 512);
INSERT INTO sync_context (owner_id, event_version)
VALUES ('00000000-0000-0000-0000-000000000004', 0);

-- Zoro
INSERT INTO user_member (id, username, password, name, dob, gender, role, version, uri, format, width, height)
VALUES ('00000000-0000-0000-0000-000000000005', 'Zoro', '$2y$10$S298DZN6DkqbjbBaP9MSYuE0ckWbCj3HyLViP.sP/zaqnoUUT6VUK',
        'Zoro', CURRENT_TIMESTAMP, 1.0, 'ROLE_USER', 0,
        'https://i.pinimg.com/736x/79/e2/c9/79e2c9402014ead1eebf6c9f184c5bf8.jpg', 'jpg', 512,
        512);
INSERT INTO sync_context (owner_id, event_version)
VALUES ('00000000-0000-0000-0000-000000000005', 0);

-- Chats
-- Luffy & Nami
INSERT INTO chat (first_user, second_user, room_name, message_count, version, icon_id)
VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', 'Luffy and Nami', 0, 0, 1);

-- Luffy & Chopper
INSERT INTO chat (first_user, second_user, room_name, message_count, version, icon_id)
VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004', 'Luffy and Chopper', 0, 0, 1);

-- Nami & Chopper
INSERT INTO chat (first_user, second_user, room_name, message_count, version, icon_id)
VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000004', 'Nami and Chopper', 0, 0, 1);

-- Chat Events
-- event1: Luffy & Nami (Nami sends Hello)
-- Luffy's sync context: 0 -> 1. event_version = 1.
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000001', '00000000-0000-0000-0000-000000000003', 'TEXT',
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0002-000000000001', 1, now() - INTERVAL '5 minutes', '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000003', now() - INTERVAL '5 minutes',
        'Hello', 0, 0, 0, 0);

-- Nami's sync context: 0 -> 1. event_version = 1.
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000002', '00000000-0000-0000-0000-000000000003', 'TEXT',
        '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0002-000000000002', 1, now() - INTERVAL '5 minutes', '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000003', now() - INTERVAL '5 minutes',
        'Hello', 0, 0, 0, 0);

-- event2: Luffy & Chopper (Chopper sends Ekk)
-- Luffy's sync context: 1 -> 2. event_version = 2.
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000003', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0002-000000000003', 2, now() - INTERVAL '10 minutes',
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000004', now() - INTERVAL '10 minutes',
        'Ekk', 0, 0, 0, 0);

-- Chopper's sync context: 0 -> 1. event_version = 1.
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000004', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0002-000000000004', 1, now() - INTERVAL '10 minutes',
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000004', now() - INTERVAL '10 minutes',
        'Ekk', 0, 0, 0, 0);

-- event3: Nami & Chopper (Chopper sends Vcl)
-- Nami's sync context: 1 -> 2. event_version = 2.
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000005', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0002-000000000005', 2, now() - INTERVAL '5 minutes', '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000004', now() - INTERVAL '5 minutes',
        'Vcl', 0, 0, 0, 0);

-- Chopper's sync context: 1 -> 2. event_version = 2.
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000006', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0002-000000000006', 2, now() - INTERVAL '5 minutes', '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000004', now() - INTERVAL '5 minutes',
        'Vcl', 0, 0, 0, 0);

-- Luffy & Chopper message events (20 messages at 10 different minutes)
-- Luffy ID: 00000000-0000-0000-0000-000000000002
-- Chopper ID: 00000000-0000-0000-0000-000000000004
-- Previous max versions: Luffy: 2, Chopper: 2

-- Minute 1: Luffy sends "Hi Chopper" & Chopper sends "Hi Luffy"
-- Luffy's events: version 3, 4
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000007', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000007', 3,
        now() - INTERVAL '20 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '20 minutes', 'Hi Chopper', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000008', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000008', 4,
        now() - INTERVAL '20 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '20 minutes', 'Hi Luffy', 0, 0, 0, 0);
-- Chopper's events: version 3, 4
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000009', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000009', 3,
        now() - INTERVAL '20 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '20 minutes', 'Hi Chopper', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000010', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000010', 4,
        now() - INTERVAL '20 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '20 minutes', 'Hi Luffy', 0, 0, 0, 0);

-- Minute 2: Luffy sends "Are you hungry?" & Chopper sends "Always!"
-- Luffy's events: version 5, 6
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000011', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000011', 5,
        now() - INTERVAL '19 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '19 minutes', 'Are you hungry?', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000012', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000012', 6,
        now() - INTERVAL '19 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '19 minutes', 'Always!', 0, 0, 0, 0);
-- Chopper's events: version 5, 6
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000013', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000013', 5,
        now() - INTERVAL '19 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '19 minutes', 'Are you hungry?', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000014', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000014', 6,
        now() - INTERVAL '19 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '19 minutes', 'Always!', 0, 0, 0, 0);

-- Minute 3: Luffy sends "Meat?" & Chopper sends "Cotton Candy!"
-- Luffy's events: version 7, 8
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000015', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000015', 7,
        now() - INTERVAL '18 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '18 minutes', 'Meat?', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000016', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000016', 8,
        now() - INTERVAL '18 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '18 minutes', 'Cotton Candy!', 0, 0, 0, 0);
-- Chopper's events: version 7, 8
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000017', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000017', 7,
        now() - INTERVAL '18 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '18 minutes', 'Meat?', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000018', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000018', 8,
        now() - INTERVAL '18 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '18 minutes', 'Cotton Candy!', 0, 0, 0, 0);

-- Minute 4: Luffy sends "Let's go to the kitchen" & Chopper sends "Sanji is there"
-- Luffy's events: version 9, 10
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000019', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000019', 9,
        now() - INTERVAL '17 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '17 minutes', 'Let''s go to the kitchen', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000020', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000020', 10,
        now() - INTERVAL '17 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '17 minutes', 'Sanji is there', 0, 0, 0, 0);
-- Chopper's events: version 9, 10
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000021', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000021', 9,
        now() - INTERVAL '17 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '17 minutes', 'Let''s go to the kitchen', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000022', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000022', 10,
        now() - INTERVAL '17 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '17 minutes', 'Sanji is there', 0, 0, 0, 0);

-- Minute 5: Luffy sends "Tell him I'm hungry" & Chopper sends "He knows already"
-- Luffy's events: version 11, 12
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000023', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000023', 11,
        now() - INTERVAL '16 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '16 minutes', 'Tell him I''m hungry', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000024', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000024', 12,
        now() - INTERVAL '16 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '16 minutes', 'He knows already', 0, 0, 0, 0);
-- Chopper's events: version 11, 12
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000025', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000025', 11,
        now() - INTERVAL '16 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '16 minutes', 'Tell him I''m hungry', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000026', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000026', 12,
        now() - INTERVAL '16 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '16 minutes', 'He knows already', 0, 0, 0, 0);

-- Minute 6: Luffy sends "Is it soup?" & Chopper sends "It's curry"
-- Luffy's events: version 13, 14
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000027', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000027', 13,
        now() - INTERVAL '15 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '15 minutes', 'Is it soup?', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000028', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000028', 14,
        now() - INTERVAL '15 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '15 minutes', 'It''s curry', 0, 0, 0, 0);
-- Chopper's events: version 13, 14
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000029', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000029', 13,
        now() - INTERVAL '15 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '15 minutes', 'Is it soup?', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000030', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000030', 14,
        now() - INTERVAL '15 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '15 minutes', 'It''s curry', 0, 0, 0, 0);

-- Minute 7: Luffy sends "Yummm" & Chopper sends "Coming?"
-- Luffy's events: version 15, 16
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000031', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000031', 15,
        now() - INTERVAL '14 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '14 minutes', 'Yummm', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000032', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000032', 16,
        now() - INTERVAL '14 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '14 minutes', 'Coming?', 0, 0, 0, 0);
-- Chopper's events: version 15, 16
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000033', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000033', 15,
        now() - INTERVAL '14 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '14 minutes', 'Yummm', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000034', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000034', 16,
        now() - INTERVAL '14 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '14 minutes', 'Coming?', 0, 0, 0, 0);

-- Minute 8: Luffy sends "On my way" & Chopper sends "Run!"
-- Luffy's events: version 17, 18
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000035', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000035', 17,
        now() - INTERVAL '13 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '13 minutes', 'On my way', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000036', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000036', 18,
        now() - INTERVAL '13 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '13 minutes', 'Run!', 0, 0, 0, 0);
-- Chopper's events: version 17, 18
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000037', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000037', 17,
        now() - INTERVAL '13 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '13 minutes', 'On my way', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000038', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000038', 18,
        now() - INTERVAL '13 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '13 minutes', 'Run!', 0, 0, 0, 0);

-- Minute 9: Luffy sends "I'm here" & Chopper sends "Quickly"
-- Luffy's events: version 19, 20
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000039', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000039', 19,
        now() - INTERVAL '12 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '12 minutes', 'I''m here', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000040', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000040', 20,
        now() - INTERVAL '12 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '12 minutes', 'Quickly', 0, 0, 0, 0);
-- Chopper's events: version 19, 20
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000041', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000041', 19,
        now() - INTERVAL '12 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '12 minutes', 'I''m here', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000042', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000042', 20,
        now() - INTERVAL '12 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '12 minutes', 'Quickly', 0, 0, 0, 0);

-- Minute 10: Luffy sends "Delicious!" & Chopper sends "I told you"
-- Luffy's events: version 21, 22
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000043', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000043', 21,
        now() - INTERVAL '11 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '11 minutes', 'Delicious!', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000044', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0002-000000000044', 22,
        now() - INTERVAL '11 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '11 minutes', 'I told you', 0, 0, 0, 0);
-- Chopper's events: version 21, 22
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000045', '00000000-0000-0000-0000-000000000002', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000045', 21,
        now() - INTERVAL '11 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '11 minutes', 'Delicious!', 0, 0, 0, 0);
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
VALUES ('00000000-0000-0000-0001-000000000046', '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0002-000000000046', 22,
        now() - INTERVAL '11 minutes', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004',
        now() - INTERVAL '11 minutes', 'I told you', 0, 0, 0, 0);

-- Chat Order
-- Luffy's chats
INSERT INTO chat_order (owner_id, current_event_id, current_version, chat_first_user, chat_second_user)
VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0001-000000000001', 1,
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000003'),
       ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0001-000000000044', 22,
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000004');

-- Nami's chats
INSERT INTO chat_order (owner_id, current_event_id, current_version, chat_first_user, chat_second_user)
VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0001-000000000002', 1,
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000003'),
       ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0001-000000000005', 2,
        '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000004');

-- Chopper's chats
INSERT INTO chat_order (owner_id, current_event_id, current_version, chat_first_user, chat_second_user)
VALUES ('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0001-000000000046', 22,
        '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000004'),
       ('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0001-000000000006', 2,
        '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000004');

-- Sync Context Updates
UPDATE sync_context
SET event_version = 22
WHERE owner_id = '00000000-0000-0000-0000-000000000002';
UPDATE sync_context
SET event_version = 2
WHERE owner_id = '00000000-0000-0000-0000-000000000003';
UPDATE sync_context
SET event_version = 22
WHERE owner_id = '00000000-0000-0000-0000-000000000004';
