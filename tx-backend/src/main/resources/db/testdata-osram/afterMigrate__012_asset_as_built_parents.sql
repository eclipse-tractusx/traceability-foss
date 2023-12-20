-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_parents
    (asset_as_built_id  , id                 , id_short)
values
    -- owner has parent customer
    (${assetAsBuiltId05}, ${assetAsBuiltId01}, 'Left Headlights'),     -- Left Head Bulb has parent Left Headlights
    (${assetAsBuiltId06}, ${assetAsBuiltId02}, 'Right Headlights'),    -- Right Head Bulb has parent Right Headlights
    (${assetAsBuiltId07}, ${assetAsBuiltId03}, 'Left Turning Light'),  --  Turning Light has parent Left Turning Light
    (${assetAsBuiltId07}, ${assetAsBuiltId04}, 'Right Turning Light'), --  Turning Light has parent Right Turning Light

    -- supplier has parent owner
    (${assetAsBuiltId09}, ${assetAsBuiltId07}, 'Turning Light'),  -- GlassBulb has parent Turning Light
    (${assetAsBuiltId10}, ${assetAsBuiltId07}, 'Turning Light');  -- Packaging has parent Turning Light
