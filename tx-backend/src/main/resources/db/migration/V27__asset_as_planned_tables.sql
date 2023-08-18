ALTER TABLE assets_as_built
    add column classification varchar(255);

create table assets_as_planned
(
    id                   varchar(255) not null,
    customer_part_id     varchar(255),
    id_short             varchar(255),
    manufacturer_part_id varchar(255),
    name_at_customer     varchar(255),
    name_at_manufacturer varchar(255),
    quality_type         integer,
    classification       varchar(255),
    owner                integer,
    semantic_data_model  integer,
    in_investigation     boolean      not null default (false),
    active_alert         boolean      NOT NULL default (false),
    primary key (id)
);

create table assets_as_planned_childs
(
    asset_as_planned_id varchar(255) not null,
    id                  varchar(255),
    id_short            varchar(255)
);

create table assets_as_planned_alerts
(
    alert_id int8         not null,
    asset_id varchar(255) not null
);

create table assets_as_planned_investigations
(
    investigation_id int8         not null,
    asset_id         varchar(255) not null
);

create table asset_as_planned_alert_notifications
(
    alert_notification_id varchar(255) not null,
    asset_id              varchar(255) not null
);

create table assets_as_planned_notifications
(
    notification_id varchar(255) not null,
    asset_id        varchar(255) not null
);





