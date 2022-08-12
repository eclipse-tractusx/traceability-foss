create table asset_entity (
    id varchar(255) not null,
    customer_part_id varchar(255),
    id_short varchar(255),
    manufacturer_id varchar(255),
    manufacturer_name varchar(255),
    manufacturer_part_id varchar(255),
    manufacturing_country varchar(255),
    manufacturing_date timestamp,
    name_at_customer varchar(255),
    name_at_manufacturer varchar(255),
    quality_type integer,
    primary key (id)
);

create table asset_entity_child_descriptors (
    asset_entity_id varchar(255) not null,
    id varchar(255),
    id_short varchar(255)
);

alter table if exists asset_entity_child_descriptors add constraint fk_asset_entity foreign key (asset_entity_id) references asset_entity;
