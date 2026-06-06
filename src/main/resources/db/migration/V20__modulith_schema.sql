create table EVENT_PUBLICATION
(
    id               uuid primary key,
    publication_date timestamptz,
    event_type       varchar(255) not null,
    serialized_event text         not null,
    listener_id      varchar(255) not null,

    completion_date  timestamptz
);