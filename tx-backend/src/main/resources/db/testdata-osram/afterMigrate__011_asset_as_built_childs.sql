-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_childs
    (asset_as_built_id  , id                 , id_short)
values
    -- owner has child of supplier
    (${assetAsBuiltId07}, ${assetAsBuiltId09}, 'S-GlassBulb'), -- Turning Light Bulb has child Glass bulb
    (${assetAsBuiltId07}, ${assetAsBuiltId10}, 'P-Packaging'), -- Turning Light Bulb has child Packaging

    -- customer has child owner
    (${assetAsBuiltId01}, ${assetAsBuiltId05}, 'Left Head Bulb'),      -- Xenon Left-Headlights has child Left Head Bulb
    (${assetAsBuiltId02}, ${assetAsBuiltId06}, 'Right Head Bulb'),     -- Xenon Right-Headlights has child Right Head Bulb
    (${assetAsBuiltId03}, ${assetAsBuiltId07}, 'Turning Light Bulb'),  -- Left Turning Lights has child Turning Light Bulb
    (${assetAsBuiltId04}, ${assetAsBuiltId07}, 'Turning Light Bulb');  -- Right Turning Lights has child Turning Light Bulb
