alter table if exists notification
    add column status varchar(255);

alter table if exists notification
    add column created timestamp with time zone;

alter table if exists notification
    add column updated timestamp with time zone;
