-- Alice (1111...) and Bob (2222...)
-- Alice (1111...) and Charlie (3333...)

-- Ensure Alice-Bob chat exists (it should be in seed_chats.sql but we might use this alone or after it)
-- This script assumes seed_users.sql and seed_chats.sql have been run.

-- We will generate 100 events for Alice in the Alice-Bob chat using standard SQL.
-- Alice ID: 11111111-1111-1111-1111-111111111111
-- Bob ID:   22222222-2222-2222-2222-222222222222

INSERT INTO chat_event (id, event_type, first_user, second_user, sender_id, owner_id, event_version,
                        created_time, idempotent_key, content)
SELECT gen_random_uuid(),
       'TEXT',
       '11111111-1111-1111-1111-111111111111',
       '22222222-2222-2222-2222-222222222222',
       '11111111-1111-1111-1111-111111111111', -- Alice is sender
       '11111111-1111-1111-1111-111111111111', -- Alice is owner
       s + 1,                                  -- Alice's version starts at 1 from seed_chats.sql
       NOW() + (s || ' seconds')::interval,
       gen_random_uuid(),
       'Seed message number ' || s
FROM generate_series(1, 100) AS s;

-- Update Alice's chat_order with Bob to point to the latest event
-- We find the event with the highest version for this chat/owner
UPDATE chat_order
SET current_event_id = (SELECT id
                        FROM chat_event
                        WHERE owner_id = '11111111-1111-1111-1111-111111111111'
                          AND first_user = '11111111-1111-1111-1111-111111111111'
                          AND second_user = '22222222-2222-2222-2222-222222222222'
                        ORDER BY event_version DESC
                        LIMIT 1),
    current_version  = 101
WHERE owner_id = '11111111-1111-1111-1111-111111111111'
  AND chat_first_user = '11111111-1111-1111-1111-111111111111'
  AND chat_second_user = '22222222-2222-2222-2222-222222222222';

-- Update Alice's sync_context
UPDATE sync_context
SET event_version = 101
WHERE owner_id = '11111111-1111-1111-1111-111111111111';

-- Update chat message count and interact time
UPDATE chat
SET message_count = message_count + 100,
    interact_time = NOW() + INTERVAL '100 seconds'
WHERE first_user = '11111111-1111-1111-1111-111111111111'
  AND second_user = '22222222-2222-2222-2222-222222222222';
