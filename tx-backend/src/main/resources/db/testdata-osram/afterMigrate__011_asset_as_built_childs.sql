-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_childs
    (asset_as_built_id  , id                 , id_short)
values
    -- supplier is child of owner
    (${assetAsBuiltId09}, ${assetAsBuiltId07}, 'Turning Light'),  -- Glass bulb for turning light isChildOf Turning Light Bulb
    (${assetAsBuiltId10}, ${assetAsBuiltId07}, 'Turning Light'),  -- Packaging for turning light isChildOf Turning Light Bulb

    -- owner is child of customer
    (${assetAsBuiltId05}, ${assetAsBuiltId01}, 'Xenon Left-Headlights'),  -- Left Head Bulb isChildOf Xenon Left-Headlights
    (${assetAsBuiltId06}, ${assetAsBuiltId02}, 'Xenon Right-Headlights'), -- Right Head Bulb isChildOf Xenon Right-Headlights
    (${assetAsBuiltId07}, ${assetAsBuiltId03}, 'Left Turning Lights'),    -- Turning Light Bulb isChildOf Left Turning Lights
    (${assetAsBuiltId07}, ${assetAsBuiltId04}, 'Right Turning Lights');   -- Turning Light Bulb isChildOf Right Turning Lights
