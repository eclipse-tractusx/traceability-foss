create table pending_investigation (
    asset_id varchar(255) not null,
    created timestamp,
    status int4,
    updated timestamp,
    primary key (asset_id)
)
