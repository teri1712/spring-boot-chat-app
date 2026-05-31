create sequence robin_seq start 1;

INSERT INTO user_member (id, username, password, name, gender, role, version)
SELECT gen_random_uuid()                                              AS id,
       'user_' || i::text                                             AS username,
       '$2y$10$gicLbaE2oVC1FRjPrkk2R.n0JTdzauXNcZ.BuNAMW.iOThJGp8pCi' AS password,
       'Performance User ' || i                                       AS name,
       FLOOR(RANDOM() * 2)                                            AS gender, -- Random gender (0 or 1)
       'ROLE_USER'                                                    AS role,
       1                                                              AS version
FROM generate_series(1, 500) AS t(i);

INSERT INTO chat (chat_id, caller_id, max_participants)
SELECT 'group_5k_perf_test'                                           AS chat_id,
       (SELECT id FROM user_member WHERE username = 'user_1' LIMIT 1) AS caller_id,
       500                                                            AS max_participants;


INSERT INTO participant (join_date, version, user_id, chat_id, write, read)
SELECT NOW() - INTERVAL '7 days' + INTERVAL '1 second' * ROW_NUMBER() OVER (ORDER BY u.id) AS join_date,
       0                                                                                   AS version,
       u.id                                                                                AS user_id,
       'group_5k_perf_test'                                                                AS chat_id,
       true                                                                                AS write,
       true                                                                                AS read
FROM user_member u
WHERE u.username LIKE 'user_%';


INSERT INTO room (chat_id, creator, representatives, id, participant_count)
SELECT 'group_5k_perf_test',
       u.id,
       jsonb_build_array(u.id),
       nextval('room_seq'),
       500
FROM user_member u
WHERE u.username = 'user_1';



INSERT INTO conversation (room_id, owner_id, recents, modified_at, hash_value, id, participant_index)
SELECT (select id from room where room.chat_id = 'group_5k_perf_test') AS room_id,
       u.id                                                            AS owner_id,
       '[]'::jsonb                                                     AS recents,
       now()                                                           AS modified_at,
       0                                                               AS hash_value,
       nextval('conversation_seq')                                     AS id,
       nextval('robin_seq') % 500
FROM user_member u
WHERE u.username LIKE 'user_%';


drop sequence robin_seq;

-- Seed 500 Text messages for the room
INSERT INTO message (sequence_id, posting_id, sender_id, message_type, chat_id, created_at, updated_at, content)
SELECT nextval('message_seq'),
       gen_random_uuid(),
       m.sender_id,
       'TEXT',
       'group_5k_perf_test',
       NOW() - INTERVAL '1 hour' + (i * INTERVAL '1 second'),
       NOW() - INTERVAL '1 hour' + (i * INTERVAL '1 second'),
       'Performance message ' || i
FROM generate_series(1, 500) AS t(i)
         CROSS JOIN LATERAL (
    SELECT id AS sender_id
    FROM user_member
    WHERE username = 'user_' || (floor(random() * 499 + (t.i * 0)) + 1)::text
    LIMIT 1
    ) m;

-- Seed 10 InboxLog per participant
INSERT INTO inbox_log (sequence_id, sender_id, owner_id, conversation_id, message_id, action)
SELECT nextval('inbox_log_seq'),
       m.sender_id,
       c.owner_id,
       c.id,
       m.sequence_id,
       'ADDITION'
FROM (SELECT id, owner_id
      FROM conversation
      WHERE room_id = (SELECT id FROM room WHERE chat_id = 'group_5k_perf_test' LIMIT 1)) c
         CROSS JOIN generate_series(1, 10) gs(j)
         CROSS JOIN LATERAL (
    SELECT sequence_id, sender_id
    FROM message
    WHERE chat_id = 'group_5k_perf_test'
      AND (c.id IS NOT NULL OR gs.j IS NOT NULL) -- Dummy dependency to force re-evaluation
    ORDER BY random()
    LIMIT 1
    ) m;
