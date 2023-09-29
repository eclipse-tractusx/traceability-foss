create table traction_battery_code_subcomponent
(
    traction_battery_code              varchar(255) not null,
    subcomponent_traction_battery_code varchar(255) not null,
    product_type                       varchar(255)
);

ALTER TABLE assets_as_built
    add column product_type varchar(255);

ALTER TABLE assets_as_built
    add column traction_battery_code varchar(255);
