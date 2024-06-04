drop schema if exists memo cascade;
create schema if not exists memo;

--account
create table memo.account(
id bigint auto_increment,
updated varchar(256),
update_datetime timestamp,
update_seq integer,
inserted varchar(256) not null,
insert_datetime timestamp not null,
login_id varchar(128) not null,
password varbinary(128),
name varchar(128) not null,
primary key(id)
);

--memo
create table memo.memo(
id bigint auto_increment,
updated varchar(256),
update_datetime timestamp,
update_seq integer,
inserted varchar(256) not null,
insert_datetime timestamp not null,
account_id bigint not null,
title varchar(128) not null,
body text not null,
primary key(id)
);
