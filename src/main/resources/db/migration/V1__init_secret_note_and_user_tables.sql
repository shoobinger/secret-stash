create table user(
    id          uuid not null primary key default gen_random_uuid(),
    name        text not null,
    email       text not null,

    password_hash text not null
)

-- TODO unique index on email

create table secret_note
(
    id          uuid not null primary key default gen_random_uuid(),
    title       text not null,
    content     text not null,
    owner_id    uuid not null references user(id),
    status      text not null,

    created_at timestamp with time zone not null,
    expires_at timestamp with time zone not null
);