-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_planned_childs
    (asset_as_planned_id  , id                   , id_short)
values
    -- supplier is child of owner
    (${assetAsPlannedId03}, ${assetAsPlannedId01}, 'H-LeftTailLight'),  -- Osram Rear Left-ZX500 isChildOf Xenon Left-Taillights
    (${assetAsPlannedId05}, ${assetAsPlannedId01}, 'H-LeftTailLight'),  -- Philips Rear Left-C4R isChildOf Xenon Left-Taillights
    (${assetAsPlannedId04}, ${assetAsPlannedId02}, 'H-RightTailLight'), -- Osram Rear Right-ZX500 isChildOf Xenon Right-Taillights
    (${assetAsPlannedId06}, ${assetAsPlannedId02}, 'H-RightTailLight'), -- Philips Rear Right-C4R isChildOf Xenon Right-Taillights

    -- owner is child of customer
    (${assetAsPlannedId01}, ${assetAsPlannedId09}, 'Audi-A7'),          -- Xenon Left-Taillights isChildOf A7
    (${assetAsPlannedId02}, ${assetAsPlannedId09}, 'Audi-A7');          -- Xenon Right-Taillights isChildOf A7
