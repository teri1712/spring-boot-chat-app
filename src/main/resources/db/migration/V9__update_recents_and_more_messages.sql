-- Insert more messages for Luffy & Chopper
INSERT INTO message (sequence_id, chat_event_id, sender_id, message_type, chat_id, created_at, updated_at, content)
VALUES (203, gen_random_uuid(), '00000000-0000-0000-0000-000000000001', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'Awesome! Tell Sanji to make the bone-in meat!'),
       (204, gen_random_uuid(), '00000000-0000-0000-0000-000000000004', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'He already knows, Luffy! Stop drooling on the Den Den Mushi!');

-- Inbox log for Luffy & Chopper
INSERT INTO inbox_log (sequence_id, chat_id, sender_id, owner_id, message_id, action, message_state)
VALUES (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 203, 'DELIVERED',
        '{"type": "text", "sequenceId": 203, "senderId": "00000000-0000-0000-0000-000000000001", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004", "content": "Awesome! Tell Sanji to make the bone-in meat!", "seenByIds": ["00000000-0000-0000-0000-000000000001"]}'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004', 203, 'DELIVERED',
        '{"type": "text", "sequenceId": 203, "senderId": "00000000-0000-0000-0000-000000000001", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004", "content": "Awesome! Tell Sanji to make the bone-in meat!", "seenByIds": ["00000000-0000-0000-0000-000000000004"]}'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', 204, 'DELIVERED',
        '{"type": "text", "sequenceId": 204, "senderId": "00000000-0000-0000-0000-000000000004", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004", "content": "He already knows, Luffy! Stop drooling on the Den Den Mushi!", "seenByIds": []}'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004',
        '00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000004', 204, 'DELIVERED',
        '{"type": "text", "sequenceId": 204, "senderId": "00000000-0000-0000-0000-000000000004", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004", "content": "He already knows, Luffy! Stop drooling on the Den Den Mushi!", "seenByIds": ["00000000-0000-0000-0000-000000000004"]}');

-- Update Recents for Luffy & Chopper
UPDATE conversation
SET recents     = '[
  {
    "type": "text",
    "sequenceId": 204,
    "senderId": "00000000-0000-0000-0000-000000000004",
    "messageType": "TEXT",
    "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004",
    "content": "He already knows, Luffy! Stop drooling on the Den Den Mushi!",
    "seenByIds": [
      "00000000-0000-0000-0000-000000000004"
    ]
  },
  {
    "type": "text",
    "sequenceId": 203,
    "senderId": "00000000-0000-0000-0000-000000000001",
    "messageType": "TEXT",
    "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004",
    "content": "Awesome! Tell Sanji to make the bone-in meat!",
    "seenByIds": [
      "00000000-0000-0000-0000-000000000001",
      "00000000-0000-0000-0000-000000000004"
    ]
  }
]'::jsonb,
    modified_at = CURRENT_TIMESTAMP
WHERE chat_id = '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004';

-- Update Last Activity
UPDATE setting
SET last_activity = CURRENT_TIMESTAMP
WHERE identifier = '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000004';


-- Insert more messages for Luffy & Zoro
INSERT INTO message (sequence_id, chat_event_id, sender_id, message_type, chat_id, created_at, updated_at, content)
VALUES (303, gen_random_uuid(), '00000000-0000-0000-0000-000000000001', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'Zoro! Just walk towards the big ship! It is hard to miss!'),
       (304, gen_random_uuid(), '00000000-0000-0000-0000-000000000005', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'The ship moved! I am sure of it! It was right here a second ago.');

-- Inbox log for Luffy & Zoro
INSERT INTO inbox_log (sequence_id, chat_id, sender_id, owner_id, message_id, action, message_state)
VALUES (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 303, 'DELIVERED',
        '{"type": "text", "sequenceId": 303, "senderId": "00000000-0000-0000-0000-000000000001", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005", "content": "Zoro! Just walk towards the big ship! It is hard to miss!", "seenByIds": ["00000000-0000-0000-0000-000000000001"]}'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000005', 303, 'DELIVERED',
        '{"type": "text", "sequenceId": 303, "senderId": "00000000-0000-0000-0000-000000000001", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005", "content": "Zoro! Just walk towards the big ship! It is hard to miss!", "seenByIds": []}'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000001', 304, 'DELIVERED',
        '{"type": "text", "sequenceId": 304, "senderId": "00000000-0000-0000-0000-000000000005", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005", "content": "The ship moved! I am sure of it! It was right here a second ago.", "seenByIds": []}'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005',
        '00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000005', 304, 'DELIVERED',
        '{"type": "text", "sequenceId": 304, "senderId": "00000000-0000-0000-0000-000000000005", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005", "content": "The ship moved! I am sure of it! It was right here a second ago.", "seenByIds": ["00000000-0000-0000-0000-000000000005"]}');

-- Update Recents for Luffy & Zoro
UPDATE conversation
SET recents     = '[
  {
    "type": "text",
    "sequenceId": 304,
    "senderId": "00000000-0000-0000-0000-000000000005",
    "messageType": "TEXT",
    "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005",
    "content": "The ship moved! I am sure of it! It was right here a second ago.",
    "seenByIds": [
      "00000000-0000-0000-0000-000000000005"
    ]
  },
  {
    "type": "text",
    "sequenceId": 303,
    "senderId": "00000000-0000-0000-0000-000000000001",
    "messageType": "TEXT",
    "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005",
    "content": "Zoro! Just walk towards the big ship! It is hard to miss!",
    "seenByIds": [
      "00000000-0000-0000-0000-000000000001"
    ]
  }
]'::jsonb,
    modified_at = CURRENT_TIMESTAMP
WHERE chat_id = '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005';

-- Update Last Activity
UPDATE setting
SET last_activity = CURRENT_TIMESTAMP
WHERE identifier = '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000005';


-- Insert more messages for Luffy & Nami
INSERT INTO message (sequence_id, chat_event_id, sender_id, message_type, chat_id, created_at, updated_at, content)
VALUES (403, gen_random_uuid(), '00000000-0000-0000-0000-000000000003', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'LUFFY!!! I’m going to double your debt!'),
       (404, gen_random_uuid(), '00000000-0000-0000-0000-000000000001', 'TEXT',
        '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP, 'EHHH?! Why?!');

-- Inbox log for Luffy & Nami
INSERT INTO inbox_log (sequence_id, chat_id, sender_id, owner_id, message_id, action, message_state)
VALUES (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 403, 'DELIVERED',
        '{"type": "text", "sequenceId": 403, "senderId": "00000000-0000-0000-0000-000000000003", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003", "content": "LUFFY!!! I’m going to double your debt!", "seenByIds": ["00000000-0000-0000-0000-000000000001"]}'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003', 403, 'DELIVERED',
        '{"type": "text", "sequenceId": 403, "senderId": "00000000-0000-0000-0000-000000000003", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003", "content": "LUFFY!!! I’m going to double your debt!", "seenByIds": ["00000000-0000-0000-0000-000000000003"]}'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 404, 'DELIVERED',
        '{"type": "text", "sequenceId": 404, "senderId": "00000000-0000-0000-0000-000000000001", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003", "content": "EHHH?! Why?!", "seenByIds": ["00000000-0000-0000-0000-000000000001"]}'),
       (nextval('inbox_log_seq'), '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003',
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003', 404, 'DELIVERED',
        '{"type": "text", "sequenceId": 404, "senderId": "00000000-0000-0000-0000-000000000001", "messageType": "TEXT", "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003", "content": "EHHH?! Why?!", "seenByIds": []}');

-- Update Recents for Luffy & Nami
UPDATE conversation
SET recents     = '[
  {
    "type": "text",
    "sequenceId": 404,
    "senderId": "00000000-0000-0000-0000-000000000001",
    "messageType": "TEXT",
    "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003",
    "content": "EHHH?! Why?!",
    "seenByIds": [
      "00000000-0000-0000-0000-000000000001"
    ]
  },
  {
    "type": "text",
    "sequenceId": 403,
    "senderId": "00000000-0000-0000-0000-000000000003",
    "messageType": "TEXT",
    "chatId": "00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003",
    "content": "LUFFY!!! I’m going to double your debt!",
    "seenByIds": [
      "00000000-0000-0000-0000-000000000001",
      "00000000-0000-0000-0000-000000000003"
    ]
  }
]'::jsonb,
    modified_at = CURRENT_TIMESTAMP
WHERE chat_id = '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003';

-- Update Last Activity
UPDATE setting
SET last_activity = CURRENT_TIMESTAMP
WHERE identifier = '00000000-0000-0000-0000-000000000001+00000000-0000-0000-0000-000000000003';


alter table message
    rename column chat_event_id to posting_id;


update room
set participant_count = 2;
