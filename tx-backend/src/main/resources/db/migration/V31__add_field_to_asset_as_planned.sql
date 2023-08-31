alter table if exists assets_as_planned
    add column validityPeriodFrom varchar (255);

alter table if exists assets_as_planned
    add column validityPeriodTo varchar (255);

alter table if exists assets_as_planned
    add column functionValidUntil varchar (255);

alter table if exists assets_as_planned
    add column functionValidFrom varchar (255);

alter table if exists assets_as_planned
    add column function varchar (255);

