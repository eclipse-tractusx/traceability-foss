alter table public.orders add column order_configuration_id varchar(255);

alter table public.order_configuration drop constraint fk_order_configuration_order;
alter table public.order_configuration drop column order_id;

