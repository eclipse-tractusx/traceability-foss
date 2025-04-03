alter table public.order_configuration drop constraint fk_order_configuration_order;
alter table public.orders alter column id type varchar(255);
alter table public.order_configuration alter column order_id type varchar(255);
alter table public.order_configuration add constraint fk_order_configuration_order foreign key (order_id) references public.orders (id);

delete from public.trigger_configuration;
insert into public.trigger_configuration (cron_expression_register_order_ttl_reached, cron_expression_map_completed_orders, part_ttl, aas_ttl, created_at, updated_at)
values ('0 */4 * * * *', '0 */4 * * * *', 2629536, 2629536, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
