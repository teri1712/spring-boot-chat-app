INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
SELECT gen_random_uuid(),
       '00000000-0000-0000-0000-000000000004',
       'TEXT',
       '00000000-0000-0000-0000-000000000004',
       gen_random_uuid(),
       22 + i,
       now() - (i || ' microseconds')::interval,
       '00000000-0000-0000-0000-000000000002',
       '00000000-0000-0000-0000-000000000004',
       now() - (i || ' microseconds')::interval,
       'I told you ' || i,
       0,
       0,
       0,
       0
FROM generate_series(1, 1000000) AS i;


INSERT INTO chat_event (id, sender_id, event_type, owner_id, idempotent_key, event_version, created_time, first_user,
                        second_user, at, content, size, icon_id, width, height)
SELECT gen_random_uuid(),
       '00000000-0000-0000-0000-000000000004',
       'TEXT',
       '00000000-0000-0000-0000-000000000002',
       gen_random_uuid(),
       22 + i,
       now() - (i || ' microseconds')::interval,
       '00000000-0000-0000-0000-000000000002',
       '00000000-0000-0000-0000-000000000004',
       now() - (i || ' microseconds')::interval,
       'Delicious! ' || i,
       0,
       0,
       0,
       0
FROM generate_series(1, 1000000) AS i;



UPDATE sync_context
SET event_version = 1000022
WHERE owner_id = '00000000-0000-0000-0000-000000000002';
UPDATE sync_context
SET event_version = 1000022
WHERE owner_id = '00000000-0000-0000-0000-000000000004';


UPDATE chat_order o
SET current_version  = 1000022,
    current_event_id = (SELECT id
                        FROM chat_event e
                        WHERE e.owner_id = o.owner_id
                          and e.first_user = o.chat_first_user
                          and e.second_user = o.chat_second_user
                        ORDER BY e.event_version DESC
                        LIMIT 1)

WHERE owner_id = '00000000-0000-0000-0000-000000000002'
  and chat_first_user = '00000000-0000-0000-0000-000000000002'
  and chat_second_user = '00000000-0000-0000-0000-000000000004';
UPDATE chat_order o
SET current_version  = 1000022,
    current_event_id = (SELECT id
                        FROM chat_event e
                        WHERE e.owner_id = o.owner_id
                          and e.first_user = o.chat_first_user
                          and e.second_user = o.chat_second_user
                        ORDER BY e.event_version DESC
                        LIMIT 1)
WHERE owner_id = '00000000-0000-0000-0000-000000000004'
  and chat_first_user = '00000000-0000-0000-0000-000000000002'
  and chat_second_user = '00000000-0000-0000-0000-000000000004';



