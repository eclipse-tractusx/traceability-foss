-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_parents
    (asset_as_built_id  , id                 , id_short)
values
    -- owner is parent of customer
    (${assetAsBuiltId01}, ${assetAsBuiltId07}, '--'), -- Xenon Left-Headlights isParentOf BMW Z1
    (${assetAsBuiltId02}, ${assetAsBuiltId07}, '--'), -- Xenon Right-Headlights isParentOf BMW Z1

    -- supplier is parent of owner
    (${assetAsBuiltId03}, ${assetAsBuiltId01}, '--'), -- Osram Left-AX400 isParentOf Xenon Left-Headlights
    (${assetAsBuiltId05}, ${assetAsBuiltId01}, '--'), -- Osram Left-AX400 isParentOf Xenon Left-Headlights
    (${assetAsBuiltId04}, ${assetAsBuiltId02}, '--'), -- Osram Right-AX400 isParentOf Xenon Right-Headlights
    (${assetAsBuiltId06}, ${assetAsBuiltId02}, '--'); -- Osram Right-AX400 isParentOf Xenon Right-Headlights
