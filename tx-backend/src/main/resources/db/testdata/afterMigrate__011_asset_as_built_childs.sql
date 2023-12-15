-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_childs
    (asset_as_built_id  , id                 , id_short)
values
    -- owner has child supplier
    (${assetAsBuiltId01}, ${assetAsBuiltId03}, 'O-LeftHeadBulb'),   -- Xenon Left-Headlights has child Osram Front Left-AX400
    (${assetAsBuiltId01}, ${assetAsBuiltId05}, 'P-LeftHeadBulb'),   -- Xenon Left-Headlights has child Philips Front Left-D3H
    (${assetAsBuiltId02}, ${assetAsBuiltId04}, 'O-RightHeadBulb'),  -- Xenon Right-Headlights has child Osram Front Right-AX400
    (${assetAsBuiltId02}, ${assetAsBuiltId06}, 'P-RightHeadBulb'),  -- Xenon Right-Headlights has child Philips Front Right-D3H

    -- customer has child owner
    (${assetAsBuiltId07}, ${assetAsBuiltId01}, 'H-LeftHeadLight'),  -- Z1 has child Xenon Left-Headlights
    (${assetAsBuiltId07}, ${assetAsBuiltId02}, 'H-RightHeadLight'); -- Z1 has child Xenon Right-Headlights
