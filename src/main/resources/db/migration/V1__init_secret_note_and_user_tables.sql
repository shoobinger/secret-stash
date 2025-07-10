create table users
(
    id          uuid not null primary key default gen_random_uuid(),
    email       text not null unique,
    password_hash text not null
);

create table secret_note
(
    id          uuid not null primary key default gen_random_uuid(),
    title       text not null,
    content     text not null,
    owner_id    uuid not null references users(id),
    status      text not null,

    created_at timestamp with time zone not null,
    expires_at timestamp with time zone
);