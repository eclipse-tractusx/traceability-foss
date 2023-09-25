alter table if exists assets_as_planned
    add column validity_period_from varchar (255);

alter table if exists assets_as_planned
    add column validity_period_to varchar (255);

alter table if exists assets_as_planned
    add column function_valid_until varchar (255);

alter table if exists assets_as_planned
    add column function_valid_from varchar (255);

alter table if exists assets_as_planned
    add column function varchar (255);

alter table if exists assets_as_planned
    add column manufacturer_name varchar (255);

alter table if exists assets_as_planned
    add column van varchar (255);
