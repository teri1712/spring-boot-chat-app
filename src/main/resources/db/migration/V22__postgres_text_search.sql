CREATE EXTENSION IF NOT EXISTS btree_gin;
create table message_history
(
    id              bigserial primary key,
    content         text not null,
    sequence_number bigint,
    chat_id         varchar(255),
    created_at      timestamptz,
    search_vector   tsvector
);

create index message_search_vector_idx on message_history using gin (chat_id, search_vector);
create trigger search_vector_update_trigger
    before update
    on message_history
    for each row
    when (new.content is distinct from old.content)
execute function tsvector_update_trigger('search_vector', 'pg_catalog.english', 'content');

create trigger search_vector_insert_trigger
    before insert
    on message_history
    for each row
execute function tsvector_update_trigger('search_vector', 'pg_catalog.english', 'content');

create table people
(
    id            bigserial primary key,
    user_id       uuid,
    username      varchar(255),
    name          varchar(255),
    gender        varchar(255),
    avatar        varchar(255),
    search_vector tsvector
);


CREATE OR REPLACE FUNCTION people_vector_trigger()
    RETURNS trigger AS
$$

BEGIN

    NEW.search_vector :=
            setweight(to_tsvector('english', coalesce(NEW.name, '')), 'A') ||
            setweight(to_tsvector('english', coalesce(NEW.username, '')), 'B');

    RETURN NEW;
END
$$ LANGUAGE plpgsql;

create trigger people_search_vector_update_trigger
    before update or insert
    on people
    for each row
execute function people_vector_trigger();


create index people_search_vector_idx on people using gin (search_vector);

create index people_user_id_idx on people using hash (user_id);