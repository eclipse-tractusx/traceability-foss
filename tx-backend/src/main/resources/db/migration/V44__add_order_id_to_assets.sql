alter table public.assets_as_built
    add column order_id varchar(255);

alter table public.assets_as_built add constraint fk_assets_as_built_order foreign key (order_id) references public.orders (id);

alter table public.assets_as_planned
    add column order_id varchar(255);

alter table public.assets_as_built add constraint fk_assets_as_planned_order foreign key (order_id) references public.orders (id);

alter table public.orders drop column order_configuration_id;
alter table public.orders add column order_configuration_id bigint;
