-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_planned_childs
    (asset_as_planned_id  , id                   , id_short)
values
    -- owner has child supplier
    (${assetAsPlannedId01}, ${assetAsPlannedId03}, 'O-LeftTailBulb'),   -- Xenon Left-Taillight has child Osram Rear Left-ZX500
    (${assetAsPlannedId01}, ${assetAsPlannedId05}, 'P-LeftTailBulb'),   -- Xenon Left-Taillight has child Philips Rear Left-C4R
    (${assetAsPlannedId02}, ${assetAsPlannedId04}, 'O-RightTailBulb'),  -- Xenon Right-Taillight has child Osram Rear Right-ZX500
    (${assetAsPlannedId02}, ${assetAsPlannedId06}, 'P-RightTailBulb'),  -- Xenon Right-Taillight has child Philips Rear Right-C4R

    -- customer has child owner
    (${assetAsPlannedId09}, ${assetAsPlannedId01}, 'H-LeftTailLight'),  -- A7 has child Xenon Left-Taillight
    (${assetAsPlannedId09}, ${assetAsPlannedId02}, 'H-RightTailLight'); -- A7 has child Xenon Right-Taillight
