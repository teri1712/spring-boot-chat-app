create sequence robin_seq start 1;

INSERT INTO user_member (id, username, password, name, gender, role, version)
SELECT gen_random_uuid()                                              AS id,
       'user_' || i::text                                             AS username,
       '$2y$10$gicLbaE2oVC1FRjPrkk2R.n0JTdzauXNcZ.BuNAMW.iOThJGp8pCi' AS password,
       'Performance User ' || i                                       AS name,
       FLOOR(RANDOM() * 2)                                            AS gender, -- Random gender (0 or 1)
       'ROLE_USER'                                                    AS role,
       1                                                              AS version
FROM generate_series(1, 100) AS t(i);

INSERT INTO chat (chat_id, caller_id, max_participants)
SELECT 'group_5k_perf_test'                                           AS chat_id,
       (SELECT id FROM user_member WHERE username = 'user_1' LIMIT 1) AS caller_id,
       100                                                            AS max_participants;


INSERT INTO participant (join_date, version, user_id, chat_id, write, read)
SELECT NOW() - INTERVAL '7 days' + INTERVAL '1 second' * ROW_NUMBER() OVER (ORDER BY u.id) AS join_date,
       0                                                                                   AS version,
       u.id                                                                                AS user_id,
       'group_5k_perf_test'                                                                AS chat_id,
       true                                                                                AS write,
       true                                                                                AS read
FROM user_member u
WHERE u.username LIKE 'user_%';


INSERT INTO room (chat_id, creator, representatives, id)
SELECT 'group_5k_perf_test',
       u.id,
       jsonb_build_array(u.id),
       nextval('room_seq')
FROM user_member u
WHERE u.username = 'user_1';



INSERT INTO conversation (chat_id, owner_id, recents, modified_at, hash_value, id, round_robin)
SELECT 'group_5k_perf_test'        AS chat_id,
       u.id                        AS owner_id,
       '[]'::jsonb                 AS recents,
       now()                       AS modified_at,
       0                           AS hash_value,
       nextval('conversation_seq') AS id,
       nextval('robin_seq') % 100
FROM user_member u
WHERE u.username LIKE 'user_%';






