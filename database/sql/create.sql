drop schema if exists tracker cascade;
create schema if not exists tracker;

--account
create table tracker.account(
id bigint auto_increment,
inserted varchar(256) not null,
insert_datetime timestamp not null,
update_seq integer not null,
updated varchar(256),
update_datetime timestamp,
login_id varchar(128) not null,
password varbinary(128),
name varchar(128) not null,
primary key(id)
);

--issue
create table tracker.issue(
id bigint auto_increment,
inserted varchar(256) not null,
insert_datetime timestamp not null,
update_seq integer not null,
updated varchar(256),
update_datetime timestamp,
account_id bigint not null,
subject varchar(128) not null,
detail text not null,
primary key(id)
);
