create table assets_as_planned_alerts
(
    alert_id int8         not null,
    asset_id varchar(255) not null
);

create table asset_as_planned_alert_notifications
(
    alert_notification_id varchar(255) not null,
    asset_id              varchar(255) not null
);

alter table if exists assets_as_planned_alerts
    add constraint fk_asset_entity foreign key (asset_id) references assets_as_planned;

alter table if exists assets_as_planned_alerts
    add constraint fk_alert foreign key (alert_id) references alert;

