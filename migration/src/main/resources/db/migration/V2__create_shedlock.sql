create table shedlock
(
    name       varchar(64)  not null,
    lock_until timestamp    null,
    locket_at  timestamp    null,
    locked_by  varchar(255) null,
    constraint shedlock_pk
        primary key (name)
);

