-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_childs
    (asset_as_built_id  , id                 , id_short)
values
    -- owner has child supplier
    (${assetAsBuiltId01}, ${assetAsBuiltId03}, 'LeftHeadLight')                      , -- Xenon Headlight left left has child Left Headbulb
    (${assetAsBuiltId01}, ${assetAsBuiltId05}, 'LeftHeadLight')                      , -- Xenon Headlight left left has child Left Xenon Gland
    (${assetAsBuiltId02}, ${assetAsBuiltId04}, 'RightHeadLight')                     , -- Xenon Headlight right right has child Right Headbulb
    (${assetAsBuiltId02}, ${assetAsBuiltId06}, 'RightHeadLight')                     , -- Xenon Headlight right has child Right Xenon Gland

    (${assetAsBuiltId15}, ${assetAsBuiltId07}, 'SO-ControlUnitSmall')                , -- Control Unit small has child Electric Fuse small
    (${assetAsBuiltId15}, ${assetAsBuiltId09}, 'SO-ControlUnitSmall')                , -- Control Unit small has child Electric Controller small
    (${assetAsBuiltId16}, ${assetAsBuiltId08}, 'SO-ControlUnitBig')                  , -- Control Unit big has child Electric Fuse big
    (${assetAsBuiltId16}, ${assetAsBuiltId10}, 'SO-ControlUnitBig')                  , -- Control Unit big has child Electric Controller big

    (${assetAsBuiltId17}, ${assetAsBuiltId12}, 'SO-TurnLight')                       , -- Turn Light has child LED Orange Turn
    (${assetAsBuiltId17}, ${assetAsBuiltId13}, 'SO-TurnLight')                       , -- Turn Light has child Case Orange
    (${assetAsBuiltId18}, ${assetAsBuiltId11}, 'SO-BreakLight')                      , -- Break Light has child LED Red Break
    (${assetAsBuiltId18}, ${assetAsBuiltId14}, 'SO-BreakLight')                      , -- Break Light has child Case Orange Turn

    -- customer has child owner
    (${assetAsBuiltId19}, ${assetAsBuiltId01}, 'MA-C4')                              , -- C4 has child Xenon Headlight left
    (${assetAsBuiltId19}, ${assetAsBuiltId02}, 'MA-C4')                              , -- C4 has child Xenon Headlight right

    (${assetAsBuiltId20}, ${assetAsBuiltId15}, 'ME-S1')                              , -- S1 has child Control Unit small
    (${assetAsBuiltId21}, ${assetAsBuiltId16}, 'ME-S1')                              , -- S1 has child Control Unit big

    (${assetAsBuiltId22}, ${assetAsBuiltId17}, 'MA-C5')                              , -- C5 has child Turn Lights
    (${assetAsBuiltId22}, ${assetAsBuiltId18}, 'MA-C5');                               -- C5 has child Break Lights
