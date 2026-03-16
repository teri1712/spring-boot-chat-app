-- Moved and fixed seed data to align with final schema (post-V6)

-- Seed users
INSERT INTO user_member (id, username, password, name, gender, role, version, avatar)
VALUES ('00000000-0000-0000-0000-000000000001', 'luffy', '$2y$10$HZEfa/O4yOb0KSrBrx5v5OXTAqW8NHFbnHczXDLpG3kmcRswdlIxu',
        'Monkey D. Luffy', 1.0, 'ROLE_USER', 1,
        'https://i.pinimg.com/736x/b0/8a/bf/b08abfaa27992a5cffbedf9669f3bb26.jpg'),
       ('00000000-0000-0000-0000-000000000003', 'nami', '$2y$10$qKnVPb3M8ej1QVccGVAqHeqYXREfcA2ylNAVIMUSzG.Qx5bTMEwLa',
        'Nami', 2.0, 'ROLE_USER', 1, 'https://i.pinimg.com/736x/74/d2/0f/74d20f3bdbeeaea11da2bf70cd1ef60a.jpg'),
       ('00000000-0000-0000-0000-000000000004', 'chopper',
        '$2y$10$nZrVCt3yQKJPSR1BFGEBmO5b/FW/Q2Hh4JkxS9XqS67HImi39agVy', 'Tony Tony Chopper', 1.0, 'ROLE_USER', 1,
        'https://i.pinimg.com/736x/ec/a7/0f/eca70f7274805de4be9553a9632778bf.jpg'),
       ('00000000-0000-0000-0000-000000000005', 'zoro', '$2y$10$hwIHyCQAQpW5xb97rdMosOwVcbMxu8.o9orulRkssI5gNuShxYmBm',
        'Roronoa Zoro', 1.0, 'ROLE_USER', 1, 'https://i.pinimg.com/736x/79/e2/c9/79e2c9402014ead1eebf6c9f184c5bf8.jpg')
ON CONFLICT (id) DO NOTHING;

-- Seed default theme
INSERT INTO theme (id, background)
VALUES (nextval('theme_seq'), 'default-background')
ON CONFLICT (id) DO NOTHING;

-- Direct Chat: Luffy + Nami
-- Chat ID: 00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003
INSERT INTO chat (chat_id, caller_id, partners, max_participants, version)
VALUES ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000001', '[
    "00000000-0000-0000-0000-000000000003"
  ]'::jsonb, 2, 1)
ON CONFLICT (chat_id) DO NOTHING;

INSERT INTO participant (user_id, chat_id, join_date, write, read, version)
VALUES ('00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003', NOW(), true, true, 1),
       ('00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003', NOW(), true, true, 1)
ON CONFLICT (user_id, chat_id) DO NOTHING;

INSERT INTO room (chat_id, creator, name, avatar, version, representatives, participant_count)
VALUES ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000001', NULL, NULL, 1, '[
    "00000000-0000-0000-0000-000000000001",
    "00000000-0000-0000-0000-000000000003"
  ]'::jsonb, 2)
ON CONFLICT (chat_id) DO NOTHING;

INSERT INTO setting (id, identifier, icon_id, room_name, room_avatar, theme_id, last_activity)
VALUES (nextval('setting_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003', 1, NULL,
        NULL, (SELECT id FROM theme WHERE background = 'default-background' LIMIT 1), NOW())
;

INSERT INTO conversation (chat_id, owner_id, version, recents, modified_at, hash_value)
VALUES ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000001', 1, '[]'::jsonb, NOW(), 1),
       ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000003', 1, '[]'::jsonb, NOW(), 2)
ON CONFLICT (chat_id, owner_id) DO NOTHING;


-- Direct Chat: Luffy + Chopper
-- Chat ID: 00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004
INSERT INTO chat (chat_id, caller_id, partners, max_participants, version)
VALUES ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001', '[
    "00000000-0000-0000-0000-000000000004"
  ]'::jsonb, 2, 1)
ON CONFLICT (chat_id) DO NOTHING;

INSERT INTO participant (user_id, chat_id, join_date, write, read, version)
VALUES ('00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004', NOW(), true, true, 1),
       ('00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004', NOW(), true, true, 1)
ON CONFLICT (user_id, chat_id) DO NOTHING;

INSERT INTO room (chat_id, creator, name, avatar, version, representatives, participant_count)
VALUES ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001', NULL, NULL, 1, '[
    "00000000-0000-0000-0000-000000000001",
    "00000000-0000-0000-0000-000000000004"
  ]'::jsonb, 2)
ON CONFLICT (chat_id) DO NOTHING;

INSERT INTO setting (id, identifier, icon_id, room_name, room_avatar, theme_id, last_activity)
VALUES (nextval('setting_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004', 1, NULL,
        NULL, (SELECT id FROM theme WHERE background = 'default-background' LIMIT 1), NOW())
;

INSERT INTO conversation (chat_id, owner_id, version, recents, modified_at, hash_value)
VALUES ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001', 1, '[]'::jsonb, NOW(), 3),
       ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000004', 1, '[]'::jsonb, NOW(), 4)
ON CONFLICT (chat_id, owner_id) DO NOTHING;


-- Direct Chat: Luffy + Zoro
-- Chat ID: 00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005
INSERT INTO chat (chat_id, caller_id, partners, max_participants, version)
VALUES ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000001', '[
    "00000000-0000-0000-0000-000000000005"
  ]'::jsonb, 2, 1)
ON CONFLICT (chat_id) DO NOTHING;

INSERT INTO participant (user_id, chat_id, join_date, write, read, version)
VALUES ('00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005', NOW(), true, true, 1),
       ('00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005', NOW(), true, true, 1)
ON CONFLICT (user_id, chat_id) DO NOTHING;

INSERT INTO room (chat_id, creator, name, avatar, version, representatives, participant_count)
VALUES ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000001', NULL, NULL, 1, '[
    "00000000-0000-0000-0000-000000000001",
    "00000000-0000-0000-0000-000000000005"
  ]'::jsonb, 2)
ON CONFLICT (chat_id) DO NOTHING;

INSERT INTO setting (id, identifier, icon_id, room_name, room_avatar, theme_id, last_activity)
VALUES (nextval('setting_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005', 1, NULL,
        NULL, (SELECT id FROM theme WHERE background = 'default-background' LIMIT 1), NOW());

INSERT INTO conversation (chat_id, owner_id, version, recents, modified_at, hash_value)
VALUES ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000001', 1, '[]'::jsonb, NOW(), 5),
       ('00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000005', 1, '[]'::jsonb, NOW(), 6)
ON CONFLICT (chat_id, owner_id) DO NOTHING;
