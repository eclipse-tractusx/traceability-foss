alter table if exists notification
    add column sender_manufacturer_name varchar(255);

alter table if exists notification
    add column receiver_manufacturer_name varchar(255);
