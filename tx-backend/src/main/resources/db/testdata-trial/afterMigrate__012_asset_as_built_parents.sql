-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_parents
    (asset_as_built_id  , id                 , id_short)
values
    -- owner has parent customer
    (${assetAsBuiltId01}, ${assetAsBuiltId19}, 'SO-XenonLeftHeadLight')                                    , -- Xenon Headlight left has parent C4
    (${assetAsBuiltId02}, ${assetAsBuiltId19}, 'SO-XenonRightHeadLight')                                   , -- Xenon Headlight right has parent C4

    (${assetAsBuiltId15}, ${assetAsBuiltId20}, 'SO-ControlUnitSmall')                                      , -- Control Unit small has parent S1
    (${assetAsBuiltId16}, ${assetAsBuiltId21}, 'SO-ControlUnitBig')                                        , -- Control Unit big has parent S2

    (${assetAsBuiltId17}, ${assetAsBuiltId22}, 'SO-TurnLight')                                             , -- Turn Light has parent C5
    (${assetAsBuiltId18}, ${assetAsBuiltId22}, 'SO-BreakLight')                                            , -- Break Light has parent C5

    -- supplier has parent owner
    (${assetAsBuiltId03}, ${assetAsBuiltId01}, 'LT-LeftHeadbulb')                                          , -- Left Headbulb has parent Xenon Headlight left
    (${assetAsBuiltId05}, ${assetAsBuiltId01}, 'BS-LeftXenonGland')                                        , -- Left Xenon Gland has parent Xenon Headlight left
    (${assetAsBuiltId04}, ${assetAsBuiltId02}, 'LT-RightHeadbulb')                                         , -- Right Headbulb has parent Xenon Headlight right
    (${assetAsBuiltId06}, ${assetAsBuiltId02}, 'BS-RightXenonGland')                                       , -- Right Xenon Gland has parent Xenon Headlight right

    (${assetAsBuiltId07}, ${assetAsBuiltId15}, 'CC-SmallElectricFuse')                                     , -- Electric Fuse small has parent Control Unit small
    (${assetAsBuiltId09}, ${assetAsBuiltId15}, 'LT-SmallElectricController')                               , -- Electric Controller small has parent Control Unit small
    (${assetAsBuiltId08}, ${assetAsBuiltId16}, 'CC-BigElectricFuse')                                       , -- Electric Fuse big has parent Control Unit big
    (${assetAsBuiltId10}, ${assetAsBuiltId16}, 'LT-BigElectricController')                                 , -- Electric Controller big has parent Control Unit big

    (${assetAsBuiltId11}, ${assetAsBuiltId18}, 'LL-RedBreakLED')                                           , -- LED Red Break has parent Break Light
    (${assetAsBuiltId12}, ${assetAsBuiltId17}, 'LL-OrangeTurnLED')                                         , -- LED Orange Turn has parent Turn Light
    (${assetAsBuiltId13}, ${assetAsBuiltId17}, 'CF-TurnCaseOrange')                                        , -- Case Orange Turn has parent Turn Light
    (${assetAsBuiltId14}, ${assetAsBuiltId18}, 'CF-BreakCaseRed');                                           -- Case Red Break has parent Break Light
