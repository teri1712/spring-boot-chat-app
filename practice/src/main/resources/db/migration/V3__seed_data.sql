-- Admin
INSERT INTO user_member (id, username, password, name, dob, role, version, width, height)
VALUES ('00000000-0000-0000-0000-000000000001', 'admin', 'admin', 'admin', CURRENT_TIMESTAMP, 'ROLE_ADMIN', 0, 512,
        512);

INSERT INTO sync_context (owner_id, event_version)
VALUES ('00000000-0000-0000-0000-000000000001', 0);

-- Themes
INSERT INTO theme (id, width, height, format)
VALUES (1, 512, 512, 'jpg');
INSERT INTO theme (id, width, height, format)
VALUES (2, 512, 512, 'jpg');
INSERT INTO theme (id, width, height, format)
VALUES (3, 512, 512, 'jpg');
INSERT INTO theme (id, width, height, format)
VALUES (4, 512, 512, 'jpg');
INSERT INTO theme (id, width, height, format)
VALUES (5, 512, 512, 'jpg');
INSERT INTO theme (id, width, height, format)
VALUES (6, 512, 512, 'jpg');
INSERT INTO theme (id, width, height, format)
VALUES (7, 512, 512, 'jpg');
INSERT INTO theme (id, width, height, format)
VALUES (8, 512, 512, 'jpg');
INSERT INTO theme (id, width, height, format)
VALUES (9, 512, 512, 'jpg');
INSERT INTO theme (id, width, height, format)
VALUES (10, 512, 512, 'jpg');

-- Users
-- Luffy
INSERT INTO user_member (id, username, password, name, dob, role, version, format, width, height)
VALUES ('00000000-0000-0000-0000-000000000002', 'Luffy', 'Luffy', 'Luffy', CURRENT_TIMESTAMP, 'ROLE_USER', 0, 'jpeg',
        512, 512);
INSERT INTO sync_context (owner_id, event_version)
VALUES ('00000000-0000-0000-0000-000000000002', 0);

-- Nami
INSERT INTO user_member (id, username, password, name, dob, role, version, format, width, height)
VALUES ('00000000-0000-0000-0000-000000000003', 'Nami', 'Nami', 'Nami', CURRENT_TIMESTAMP, 'ROLE_USER', 0, 'jpeg', 512,
        512);
INSERT INTO sync_context (owner_id, event_version)
VALUES ('00000000-0000-0000-0000-000000000003', 0);

-- Chopper
INSERT INTO user_member (id, username, password, name, dob, role, version, format, width, height)
VALUES ('00000000-0000-0000-0000-000000000004', 'Chopper', 'Chopper', 'Chopper', CURRENT_TIMESTAMP, 'ROLE_USER', 0,
        'jpeg', 512, 512);
INSERT INTO sync_context (owner_id, event_version)
VALUES ('00000000-0000-0000-0000-000000000004', 0);

-- Zoro
INSERT INTO user_member (id, username, password, name, dob, role, version, format, width, height)
VALUES ('00000000-0000-0000-0000-000000000005', 'Zoro', 'Zoro', 'Zoro', CURRENT_TIMESTAMP, 'ROLE_USER', 0, 'jpg', 512,
        512);
INSERT INTO sync_context (owner_id, event_version)
VALUES ('00000000-0000-0000-0000-000000000005', 0);

-- Chats
-- Luffy & Nami
INSERT INTO chat (first_user, second_user, message_count, version, resource_id)
VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003', 0, 0, 0);

-- Luffy & Chopper
INSERT INTO chat (first_user, second_user, message_count, version, resource_id)
VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004', 0, 0, 0);

-- Nami & Chopper
INSERT INTO chat (first_user, second_user, message_count, version, resource_id)
VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000004', 0, 0, 0);

-- Chat Events
-- event1: Luffy & Nami (Nami sends Hello)
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, resource_id, width, height)
VALUES (gen_random_uuid(), '00000000-0000-0000-0000-000000000003', 'TEXT', '00000000-0000-0000-0000-000000000003',
        gen_random_uuid(), 0, now() - INTERVAL '5 minutes', '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000003', now() - INTERVAL '5 minutes',
        'Hello', 0, 0, 0, 0);

-- event2: Luffy & Chopper (Chopper sends Ekk)
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, resource_id, width, height)
VALUES (gen_random_uuid(), '00000000-0000-0000-0000-000000000004', 'TEXT', '00000000-0000-0000-0000-000000000004',
        gen_random_uuid(), 0, now() - INTERVAL '10 minutes', '00000000-0000-0000-0000-000000000002',
        '00000000-0000-0000-0000-000000000004', now() - INTERVAL '10 minutes',
        'Ekk', 0, 0, 0, 0);

-- event3: Nami & Chopper (Chopper sends Vcl)
INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, resource_id, width, height)
VALUES (gen_random_uuid(), '00000000-0000-0000-0000-000000000004', 'TEXT', '00000000-0000-0000-0000-000000000004',
        gen_random_uuid(), 0, now() - INTERVAL '5 minutes', '00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000004', now() - INTERVAL '5 minutes',
        'Vcl', 0, 0, 0, 0);
