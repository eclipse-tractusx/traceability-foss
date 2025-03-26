CREATE TABLE public.orders
(
    id                  bigint primary key not null,
    status              varchar(11),
    message             text,
    created_at          timestamptz,
    updated_at          timestamptz
);
