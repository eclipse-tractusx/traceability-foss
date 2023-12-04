-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_planned_childs
    (asset_as_planned_id  , id                   , id_short)
values
    -- supplier is child of owner
    (${assetAsPlannedId09}, ${assetAsPlannedId07}, 'Turning Light'),  -- GlassBulb isChildOf Turning Light
    (${assetAsPlannedId10}, ${assetAsPlannedId07}, 'Turning Light'),  -- Packaging isChildOf Turning Light

    -- owner is child of customer
    (${assetAsPlannedId05}, ${assetAsPlannedId01}, 'Xenon Left-Headlights'),  -- Left Head Bulb isChildOf Xenon Left-Headlights
    (${assetAsPlannedId06}, ${assetAsPlannedId02}, 'Xenon Right-Headlights'), -- Right Head Bulb isChildOf Xenon Right-Headlights
    (${assetAsPlannedId07}, ${assetAsPlannedId03}, 'Left Turning Lights'),    -- Turning Lights isChildOf Left Turning Lights
    (${assetAsPlannedId07}, ${assetAsPlannedId04}, 'Right Turning Lights');   -- Turning Lights isChildOf Right Turning Lights
