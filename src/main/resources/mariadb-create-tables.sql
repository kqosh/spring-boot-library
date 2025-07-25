
    create sequence author_seq start with 1 increment by 50;

    create sequence authorities_seq start with 1 increment by 50;

    create sequence book_seq start with 1 increment by 50;

    create sequence checkout_seq start with 1 increment by 50;

    create table author (
        id bigint not null,
        name varchar(255) not null unique,
        primary key (id)
    );

    create table authorities (
        id bigint not null,
        authority varchar(50) not null,
        username varchar(50) not null,
        primary key (id),
        constraint uc_auth_username unique (username, authority)
    );

    create table book (
        id bigint not null,
        isbn varchar(255) not null unique,
        author_id bigint,
        title varchar(255),
        number_of_copies integer not null,
        price_in_cent integer not null,
        publisher varchar(255),
        primary key (id)
    );

    create table checkout (
        id bigint not null,
        username varchar(50) not null,
        book_id bigint not null,
        renew_count integer not null,
        returned boolean not null,
        checkout_at timestamp(6) not null,
        due_date timestamp(6) not null,
        primary key (id),
        constraint uc_book_users_checkoutat unique (book_id, username, checkout_at)
    );

    create table users (
        username varchar(50) not null,
        password varchar(500) not null,
        enabled boolean not null,
        loan_period_in_days integer not null,
        max_renew_count integer not null,
        outstanding_balance_in_cent integer not null default 0,
        primary key (username)
    );

    create index idx_author_name 
       on author (name);

    create index idx_book_title 
       on book (title);

    alter table if exists authorities 
       add constraint fk_authorities_users 
       foreign key (username) 
       references users;

    alter table if exists book 
       add constraint fk_book_author 
       foreign key (author_id) 
       references author (id);

    alter table if exists checkout 
       add constraint fk_checkout_book 
       foreign key (book_id) 
       references book (id);

    alter table if exists checkout 
       add constraint fk_checkout_users 
       foreign key (username) 
       references users (username);
