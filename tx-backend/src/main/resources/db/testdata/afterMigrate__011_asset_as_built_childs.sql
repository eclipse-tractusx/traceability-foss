-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_childs
    (asset_as_built_id  , id                 , id_short)
values
    -- supplier is child of owner
    (${assetAsBuiltId03}, ${assetAsBuiltId01}, 'H-LeftHeadLight'),  -- Osram Front Left-AX400 isChildOf Xenon Left-Headlights
    (${assetAsBuiltId05}, ${assetAsBuiltId01}, 'H-LeftHeadLight'),  -- Phillips Front Left-D3H isChildOf Xenon Left-Headlights
    (${assetAsBuiltId04}, ${assetAsBuiltId02}, 'H-RightHeadLight'), -- Osram Front Right-AX400 isChildOf Xenon Right-Headlights
    (${assetAsBuiltId06}, ${assetAsBuiltId02}, 'H-RightHeadLight'), -- Phillips Front Right-D3H isChildOf Xenon Right-Headlights

    -- owner is child of customer
    (${assetAsBuiltId01}, ${assetAsBuiltId07}, 'BMW-Z1'),           -- Xenon Left-Headlights isChildOf Z1
    (${assetAsBuiltId02}, ${assetAsBuiltId07}, 'BMW-Z1');           -- Xenon Right-Headlights isChildOf Z1
