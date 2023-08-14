alter table if exists investigation_notification
    add column errorMessage varchar(255);

alter table if exists alert_notification
    add column errorMessage varchar(255);
