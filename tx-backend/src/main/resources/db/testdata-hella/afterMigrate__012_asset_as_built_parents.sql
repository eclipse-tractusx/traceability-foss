-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_parents
    (asset_as_built_id  , id                 , id_short)
values
    -- owner has parent customer
    (${assetAsBuiltId01}, ${assetAsBuiltId07}, 'BMW-Z1'),  -- Xenon Left-Headlights has parent Z1
    (${assetAsBuiltId02}, ${assetAsBuiltId07}, 'BMW-Z1'), -- Xenon Right-Headlights has parent Z1

    -- supplier has parent owner
    (${assetAsBuiltId03}, ${assetAsBuiltId01}, 'H-LeftHeadLight'),   -- Osram Front Left-AX400 has parent Xenon Left-Headlights
    (${assetAsBuiltId05}, ${assetAsBuiltId01}, 'H-LeftHeadLight'),   -- Philips Front Left-D3H has parent Xenon Left-Headlights
    (${assetAsBuiltId04}, ${assetAsBuiltId02}, 'H-RightHeadLight'),  -- Osram Front Right-AX400 has parent Xenon Right-Headlights
    (${assetAsBuiltId06}, ${assetAsBuiltId02}, 'H-RightHeadLight');  -- Philips Front Right-D3H has parent Xenon Right-Headlights
