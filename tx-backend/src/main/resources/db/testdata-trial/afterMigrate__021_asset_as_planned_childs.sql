-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_planned_childs
    (asset_as_planned_id  , id                   , id_short)
values
    -- owner has child of supplier
    (${assetAsPlannedId07}, ${assetAsPlannedId09}, 'S-GlassBulb')                                                             , -- Turning Light Bulb has child Glass bulb
    (${assetAsPlannedId07}, ${assetAsPlannedId10}, 'P-Packaging')                                                             , -- Turning Light Bulb has child Packaging

    -- customer has child owner
    (${assetAsPlannedId01}, ${assetAsPlannedId05}, 'Left Head Bulb')                                                          , -- Xenon Left-Headlights has child Left Head Bulb
    (${assetAsPlannedId02}, ${assetAsPlannedId06}, 'Right Head Bulb')                                                         , -- Xenon Right-Headlights has child Right Head Bulb
    (${assetAsPlannedId03}, ${assetAsPlannedId07}, 'Turning Light Bulb')                                                      , -- Left Turning Lights has child Turning Light Bulb
    (${assetAsPlannedId04}, ${assetAsPlannedId07}, 'Turning Light Bulb');                                                       -- Right Turning Lights has child Turning Light Bulb
