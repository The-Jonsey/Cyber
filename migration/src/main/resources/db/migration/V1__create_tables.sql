create table file
(
    id binary(16) not null,
    filename varchar(255) not null,
    uploaded datetime not null,
    rows int not null,
    constraint file_pk
        primary key (id)
);


create table log
(
    id binary(16) not null,
    row varchar(512) not null,
    count int not null,
    file_id binary(16) null,
    constraint log_pk
        primary key (id)
);

create table filerow
(
    id binary(16) not null,
    file_id binary(16) not null,
    row varchar(512) not null,
    count bigint not null,
    constraint filerow_pk
        primary key (id)
);

