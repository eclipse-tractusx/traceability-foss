alter table if exists assets_as_planned
    add column semantic_model_id varchar (255);

alter table if exists assets_as_planned
drop
column name_at_customer;

alter table if exists assets_as_planned
drop
column customer_part_id;
