-- IDs for users
-- Alice: 11111111-1111-1111-1111-111111111111
-- Bob:   22222222-2222-2222-2222-222222222222
-- Charlie: 33333333-3333-3333-3333-333333333333

INSERT INTO user_member (id, username, password, name, gender, role, version)
VALUES ('11111111-1111-1111-1111-111111111111', 'alice', '$2y$10$N6IxJMEd2BsIiXL2fd.c.eBtgM1FYBLKa9A4TThQXluVXnSvNfcYm',
        'Alice Liddell', 2.0, 'ROLE_USER', 1),
       ('22222222-2222-2222-2222-222222222222', 'bob', '$2y$10$N6IxJMEd2BsIiXL2fd.c.eBtgM1FYBLKa9A4TThQXluVXnSvNfcYm',
        'Bob Builder', 1.0, 'ROLE_USER', 1),
       ('33333333-3333-3333-3333-333333333333', 'charlie',
        '$2y$10$N6IxJMEd2BsIiXL2fd.c.eBtgM1FYBLKa9A4TThQXluVXnSvNfcYm', 'Charlie Brown', 1.0, 'ROLE_USER', 1);

INSERT INTO sync_context (owner_id, event_version)
VALUES ('11111111-1111-1111-1111-111111111111', 0),
       ('22222222-2222-2222-2222-222222222222', 0),
       ('33333333-3333-3333-3333-333333333333', 0);
