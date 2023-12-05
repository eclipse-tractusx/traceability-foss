-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_parents
    (asset_as_built_id  , id                 , id_short)
values
    -- owner is parent of supplier
    (${assetAsBuiltId01}, ${assetAsBuiltId07}, 'BMW-Z1'),           -- Xenon Left-Headlights isChildOf Z1
    (${assetAsBuiltId02}, ${assetAsBuiltId07}, 'BMW-Z1');           -- Xenon Right-Headlights isChildOf Z1
