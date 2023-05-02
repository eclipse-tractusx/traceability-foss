alter table if exists asset
    add column in_investigation boolean not null default (false);
