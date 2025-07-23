drop schema if exists tracker cascade;
create schema if not exists tracker;

--account
create table tracker.account(
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

--issue
create table tracker.issue(
id bigint auto_increment,
updated varchar(256),
update_datetime timestamp,
update_seq integer,
inserted varchar(256) not null,
insert_datetime timestamp not null,
account_id bigint not null,
subject varchar(128) not null,
detail text not null,
primary key(id)
);
