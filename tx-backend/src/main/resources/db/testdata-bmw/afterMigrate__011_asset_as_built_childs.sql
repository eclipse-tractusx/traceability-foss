-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_childs
    (asset_as_built_id  , id                 , id_short)
values
    -- owner has child supplier
    (${assetAsBuiltId07}, ${assetAsBuiltId01}, 'H-LeftHeadLight'),  -- Z1 has child Xenon Left-Headlights
    (${assetAsBuiltId07}, ${assetAsBuiltId02}, 'H-RightHeadLight'); -- Z1 has child Xenon Right-Headlights
