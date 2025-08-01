create table public_link (
    id uuid primary key,
    note_id uuid not null,
    expires_at  timestamp with time zone not null,
)