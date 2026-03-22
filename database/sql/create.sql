drop schema if exists pj cascade;
create schema if not exists pj;

--account
create table pj.account(
id bigint auto_increment,
inserted varchar(256) not null,
insert_datetime timestamp not null,
updated varchar(256),
update_datetime timestamp,
update_seq integer not null default 0,
login_id varchar(128) not null,
password varbinary(128),
name varchar(128) not null,
primary key(id)
);

-- project
create table pj.project(
id bigint auto_increment,
inserted varchar(256) not null,
insert_datetime timestamp not null,
updated varchar(256),
update_datetime timestamp,
update_seq integer not null default 0,
account_id bigint not null,
customer_id bigint not null,
start_schedule_date date,
end_schedule_date date,
start_date date,
end_date date,
subject varchar(256) not null,
description text,
primary key(id)
);

-- task
create table pj.task(
id bigint auto_increment,
inserted varchar(256) not null,
insert_datetime timestamp not null,
updated varchar(256),
update_datetime timestamp,
update_seq integer not null default 0,
project_id bigint not null,
priority int,
cost double,
start_datetime timestamp,
subject varchar(256) not null,
description text,
progress int,
complete_datetime timestamp,
primary key(id)
);

-- task_to_account
create table pj.task_to_account(
id bigint auto_increment,
inserted varchar(256) not null,
insert_datetime timestamp not null,
updated varchar(256),
update_datetime timestamp,
update_seq integer not null default 0,
task_id bigint not null,
account_id bigint not null,
primary key(id)
);

-- customer
create table pj.customer(
id bigint auto_increment,
inserted varchar(256) not null,
insert_datetime timestamp not null,
updated varchar(256),
update_datetime timestamp,
update_seq integer not null default 0,
company_name varchar(64),
pic_name varchar(64),
email_address varchar(256),
telephon_number varchar(11),
fax_number varchar(11),
address varchar(128),
building varchar(64),
primary key(id)
);

