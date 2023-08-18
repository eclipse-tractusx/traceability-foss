create table asset_parent_descriptors
(
    asset_entity_id varchar(255) not null,
    id              varchar(255),
    id_short        varchar(255)
);

alter table if exists asset_parent_descriptors add constraint fk_asset foreign key (asset_entity_id) references asset;

alter table if exists asset
    add column owner int4;

alter table if exists asset DROP COLUMN supplier_part;

