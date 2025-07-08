create type secret_note_status as enum ('ACTIVE', 'DELETED', 'EXPIRED');

create table secret_note
(
    id          uuid not null primary key,
    title       text not null,
    content     text not null,
    owner_id    uuid not null,
    status      secret_note_status not null,

    created_at timestamp with time zone not null,
    expires_at timestamp with time zone not null
);