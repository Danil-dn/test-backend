create table author
(
    id         serial primary key,
    full_name  text      not null,
    cdat timestamp not null default now()
);