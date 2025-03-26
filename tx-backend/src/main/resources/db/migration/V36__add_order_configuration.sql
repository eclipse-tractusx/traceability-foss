CREATE TABLE public.order_configuration
(
    id                  bigserial primary key not null,
    order_id            bigint,
    batch_size          integer,
    timeout_ms          integer,
    job_timeout_ms      integer,
    created_at          timestamptz,
    updated_at          timestamptz,
    constraint fk_order_configuration_order foreign key (order_id) references public.orders (id)
);

insert into public.order_configuration (batch_size, timeout_ms, job_timeout_ms, created_at, updated_at) values ( 100, 1800000, 1800000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
