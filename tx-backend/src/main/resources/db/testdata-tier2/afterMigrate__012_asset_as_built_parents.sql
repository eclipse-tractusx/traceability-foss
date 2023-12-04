-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_parents
    (asset_as_built_id  , id                 , id_short)
values
    -- customer is parent of owner
    (${assetAsBuiltId01}, ${assetAsBuiltId05}, 'Left Head Bulb'),   -- Left Headlights isParentOf Left Head Bulb
    (${assetAsBuiltId02}, ${assetAsBuiltId06}, 'Right Head Bulb'),  -- Right Headlights isParentOf Right Head Bulb
    (${assetAsBuiltId03}, ${assetAsBuiltId07}, 'Turning Light'),    -- Left Turning Light isParentOf Turning Light
    (${assetAsBuiltId04}, ${assetAsBuiltId07}, 'Turning Light'),    -- Right Turning Light isParentOf Turning Light

    -- owner is parent of supplier
    (${assetAsBuiltId07}, ${assetAsBuiltId09}, 'Turning Light'),  -- Turning Light isParentOf GlassBulb
    (${assetAsBuiltId07}, ${assetAsBuiltId10}, 'Turning Light');  -- Turning Light isParentOf Packaging
