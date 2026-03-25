-- Private chat: Luffy & Chopper
-- Private chat: Luffy & Zoro

-- Messages: Luffy & Chopper
INSERT INTO message (sequence_id, chat_event_id, sender_id, message_type, chat_id, created_at, updated_at, content)
VALUES (201, gen_random_uuid(), '00000000-0000-0000-0000-000000000001', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'Chopper! Do we have any meat?'),
       (202, gen_random_uuid(), '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'Luffy! You just ate 10 minutes ago! But Sanji is preparing some.');

INSERT INTO inbox_log (sequence_id, chat_id, sender_id, owner_id, message_id, action, message_state)
VALUES (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 201, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Chopper! Do we have any meat?",
          "seenBy": [
            "00000000-0000-0000-0000-000000000001"
          ]
        }'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004', 201, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Chopper! Do we have any meat?",
          "seenBy": [
            "00000000-0000-0000-0000-000000000004"
          ]
        }'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', 202, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Luffy! You just ate 10 minutes ago! But Sanji is preparing some.",
          "seenBy": []
        }'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000004', 202, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Luffy! You just ate 10 minutes ago! But Sanji is preparing some.",
          "seenBy": [
            "00000000-0000-0000-0000-000000000004"
          ]
        }');

INSERT INTO seen_pointer (id, sender_id, chat_id, at, message_id)
VALUES (nextval('seen_pointer_seq'), '00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP, 201),
       (nextval('seen_pointer_seq'), '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP, 201);


-- Messages: Luffy & Zoro
INSERT INTO message (sequence_id, chat_event_id, sender_id, message_type, chat_id, created_at, updated_at, content)
VALUES (301, gen_random_uuid(), '00000000-0000-0000-0000-000000000001', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'Zoro, where are you? We are at the port!'),
       (302, gen_random_uuid(), '00000000-0000-0000-0000-000000000005', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        'I am already at the port! I just took a turn and now I am in a forest with huge mushrooms...');

INSERT INTO inbox_log (sequence_id, chat_id, sender_id, owner_id, message_id, action, message_state)
VALUES (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 301, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Zoro, where are you? We are at the port!",
          "seenBy": [
            "00000000-0000-0000-0000-000000000001"
          ]
        }'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000005', 301, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Zoro, where are you? We are at the port!",
          "seenBy": []
        }'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000001', 302, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "I am already at the port! I just took a turn and now I am in a forest with huge mushrooms...",
          "seenBy": []
        }'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000005', 302, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "I am already at the port! I just took a turn and now I am in a forest with huge mushrooms...",
          "seenBy": [
            "00000000-0000-0000-0000-000000000005"
          ]
        }');


-- Additional messages: Luffy & Nami
INSERT INTO message (sequence_id, chat_event_id, sender_id, message_type, chat_id, created_at, updated_at, content)
VALUES (401, gen_random_uuid(), '00000000-0000-0000-0000-000000000003', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'Luffy, don’t you dare spend all our money on food again!'),
       (402, gen_random_uuid(), '00000000-0000-0000-0000-000000000001', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'Shishishi! Too late, Nami!');

INSERT INTO inbox_log (sequence_id, chat_id, sender_id, owner_id, message_id, action, message_state)
VALUES (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 401, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Luffy, don’t you dare spend all our money on food again!",
          "seenBy": [
            "00000000-0000-0000-0000-000000000001"
          ]
        }'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003', 401, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Luffy, don’t you dare spend all our money on food again!",
          "seenBy": [
            "00000000-0000-0000-0000-000000000003"
          ]
        }'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 402, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Shishishi! Too late, Nami!",
          "seenBy": [
            "00000000-0000-0000-0000-000000000001"
          ]
        }'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 402, 'DELIVERED',
        '{
          "type": "TEXT",
          "content": "Shishishi! Too late, Nami!",
          "seenBy": []
        }');

INSERT INTO seen_pointer (id, sender_id, chat_id, at, message_id)
VALUES (nextval('seen_pointer_seq'), '00000000-0000-0000-0000-000000000001',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP, 401);
