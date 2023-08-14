alter table if exists investigation
    add column error_message varchar(255);

alter table if exists alert
    add column error_message varchar(255);
