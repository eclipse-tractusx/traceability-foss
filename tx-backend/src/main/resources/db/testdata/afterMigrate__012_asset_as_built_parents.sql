-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_parents
    (asset_as_built_id  , id                 , id_short)
values
    -- customer is parent of owner
    (${assetAsBuiltId07}, ${assetAsBuiltId01}, 'H-LeftHeadLight'),  -- Z1 isParentOf Xenon Left-Headlights
    (${assetAsBuiltId07}, ${assetAsBuiltId02}, 'H-RightHeadLight'), -- Z1 isParentOf Xenon Right-Headlights

    -- owner is parent of supplier
    (${assetAsBuiltId01}, ${assetAsBuiltId03}, 'O-LeftHeadBulb'),   -- Xenon Left-Headlights isParentOf Osram Front Left-AX400
    (${assetAsBuiltId01}, ${assetAsBuiltId05}, 'P-LeftHeadBulb'),   -- Xenon Left-Headlights isParentOf Phillips Front Left-D3H
    (${assetAsBuiltId02}, ${assetAsBuiltId04}, 'O-RightHeadBulb'),  -- Xenon Right-Headlights isParentOf Osram Front Right-AX400
    (${assetAsBuiltId02}, ${assetAsBuiltId06}, 'P-RightHeadBulb');  -- Xenon Right-Headlights isParentOf Phillips Front Right-D3H
