CREATE TABLE public.trigger_configuration
(
    id                                            bigserial primary key not null,
    cron_expression_register_order_ttl_reached    text,
    cron_expression_map_completed_orders          text,
    part_ttl                                      integer,
    aas_ttl                                       integer,
    created_at                                    timestamptz,
    updated_at                                    timestamptz
);

insert into public.trigger_configuration (cron_expression_register_order_ttl_reached, cron_expression_map_completed_orders, part_ttl, aas_ttl, created_at, updated_at)
values ('0 */4 * * *', '0 */4 * * *', 2629536, 2629536, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

alter table public.assets_as_built add column ttl integer null;
alter table public.assets_as_planned add column ttl integer null;

alter table public.assets_as_built add column expiration_date timestamptz;
alter table public.assets_as_planned add column expiration_date timestamptz;
