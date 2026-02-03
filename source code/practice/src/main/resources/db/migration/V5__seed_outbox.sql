-- Seed UserCreatedEvent from user_member
INSERT INTO outbox (id, topic, key, payload, created_at, status)
SELECT nextval('outbox_seq'),
       'users',
       u.username,
       jsonb_build_object(
               'userId', u.id,
               'username', u.username,
               'name', u.name,
               'gender', u.gender,
               'dob', u.dob,
               'avatar', jsonb_build_object(
                       'uri', COALESCE(u.uri, ''),
                       'filename', COALESCE(u.filename, ''),
                       'width', COALESCE(u.width, 512),
                       'height', COALESCE(u.height, 512),
                       'format', COALESCE(u.format, 'jpg')
                         )
       ),
       now(),
       CAST('PENDING' AS outbox_status)
FROM user_member u;

-- Seed EventDto from chat_event
INSERT INTO outbox (id, topic, key, payload, created_at, status)
SELECT nextval('outbox_seq'),
       'messages',
       e.idempotent_key::varchar(255),
       jsonb_build_object(
               'idempotencyKey', e.idempotent_key,
               'sender', e.sender_id,
               'chat', jsonb_build_object(
                       'identifier', jsonb_build_object(
                       'firstUser', e.first_user,
                       'secondUser', e.second_user
                                     ),
                       'owner', e.owner_id,
                       'partner', (CASE
                                       WHEN e.first_user = e.owner_id THEN e.second_user
                                       ELSE e.first_user END)
                       ),
               'partner', (SELECT jsonb_build_object(
                                          'id', u.id,
                                          'username', u.username,
                                          'name', u.name,
                                          'dob', u.dob,
                                          'role', u.role,
                                          'gender', u.gender,
                                          'avatar', jsonb_build_object(
                                                  'uri', COALESCE(u.uri, ''),
                                                  'filename', COALESCE(u.filename, ''),
                                                  'width', COALESCE(u.width, 512),
                                                  'height', COALESCE(u.height, 512),
                                                  'format', COALESCE(u.format, 'jpg')
                                                    )
                                  )
                           FROM user_member u
                           WHERE u.id = (CASE
                                             WHEN e.first_user = e.owner_id THEN e.second_user
                                             ELSE e.first_user END)),
               'textEvent', CASE
                                WHEN e.event_type = 'TEXT' THEN jsonb_build_object('content', e.content)
                                ELSE NULL END
       ),
       e.created_time,
       CAST('PENDING' AS outbox_status)
FROM chat_event e
WHERE e.event_type = 'TEXT';
