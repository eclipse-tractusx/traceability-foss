-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_planned
    (id                   , id_short             , manufacturer_part_id, name_at_manufacturer        , quality_type, classification, owner     , semantic_data_model, function    , manufacturer_name, van , semantic_model_id          , catenax_site_id   , function_valid_from  , function_valid_until , validity_period_from , validity_period_to)
values
    (${assetAsPlannedId01}, 'H-LeftTailLight'    , 'XT2309'            , 'Xenon Left-Taillight'      , 'OK'        , 'C-Level'     , 'OWN'     , 'PARTASPLANNED'    , 'production', 'Hella'          , '--', '5739525733343254979259249', 'BPNL000000000001', '2019-03-04T10:00:00', '2025-03-04T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId02}, 'H-RightTailLight'   , 'XT2310'            , 'Xenon Right-Taillight'     , 'OK'        , 'C-Level'     , 'OWN'     , 'PARTASPLANNED'    , 'production', 'Hella'          , '--', '3555795495432474727732252', 'BPNL000000000001', '2019-03-04T16:00:00', '2025-03-04T16:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId03}, 'O-LeftTailBulb'     , 'LBT910'            , 'Osram Rear Left-ZX500'     , 'OK'        , 'C-Level'     , 'SUPPLIER', 'PARTASPLANNED'    , 'production', 'Osram'          , '--', '7557973754595993573779235', 'BPNL000SUPPLIER1', '2019-02-05T10:00:00', '2025-02-05T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId04}, 'O-RightTailBulb'    , 'LBT920'            , 'Osram Rear Right-ZX500'    , 'OK'        , 'C-Level'     , 'SUPPLIER', 'PARTASPLANNED'    , 'production', 'Osram'          , '--', '7724524552224773742557429', 'BPNL000SUPPLIER1', '2019-02-05T16:00:00', '2025-02-05T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId05}, 'P-LeftTailBulb'     , 'C4RPTL'            , 'Philips Rear Left-C4R'     , 'OK'        , 'C-Level'     , 'SUPPLIER', 'PARTASPLANNED'    , 'production', 'Philips'        , '--', '7259534753592972592444239', 'BPNL000SUPPLIER2', '2019-02-08T10:00:00', '2025-02-08T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId06}, 'P-RightTailBulb'    , 'C4RPTR'            , 'Philips Rear Right-C4R'    , 'OK'        , 'C-Level'     , 'SUPPLIER', 'PARTASPLANNED'    , 'production', 'Philips'        , '--', '4922952344449439397957339', 'BPNL000SUPPLIER2', '2019-02-08T10:00:00', '2025-02-08T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId07}, 'BMW-Z1'             , 'Z1ABC'             , 'Z1'                        , 'OK'        , 'C-Level'     , 'CUSTOMER', 'PARTASPLANNED'    , 'production', 'BMW AG'         , '--', '7922333444399397355743927', 'BPNL000CUSTOMER1', '2019-07-30T10:00:00', '2025-07-30T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId08}, 'BMW-Z4'             , 'Z4ABC'             , 'Z3'                        , 'OK'        , 'C-Level'     , 'CUSTOMER', 'PARTASPLANNED'    , 'production', 'BMW AG'         , '--', '4943443393237449573773535', 'BPNL000CUSTOMER1', '2019-07-31T10:00:00', '2025-07-31T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId09}, 'Audi-A7'            , 'A7XXX'             , 'A7'                        , 'OK'        , 'C-Level'     , 'CUSTOMER', 'PARTASPLANNED'    , 'production', 'Audi AG'        , '--', '9923399774344455937394339', 'BPNL000CUSTOMER2', '2019-08-10T10:00:00', '2025-08-10T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId10}, 'Audi-A8'            , 'A8XXX'             , 'A8'                        , 'OK'        , 'C-Level'     , 'CUSTOMER', 'PARTASPLANNED'    , 'production', 'Audi AG'        , '--', '5422724375732744449977947', 'BPNL000CUSTOMER2', '2019-08-10T10:00:00', '2025-08-10T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId11}, 'H-LeftTurningLight' , 'XH4713'            , 'Left Turning Lights'       , 'OK'        , 'C-Level'     , 'OWN'     , 'PARTASPLANNED'    , 'production', 'Hella'          , '--', '9497103120176833301945729', 'BPNL000000000001', '2019-03-04T10:00:00', '2025-03-04T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId12}, 'H-RightTurningLight', 'XH4714'            , 'Right Turning Lights'      , 'OK'        , 'C-Level'     , 'OWN'     , 'PARTASPLANNED'    , 'production', 'Hella'          , '--', '6804027053225747089496937', 'BPNL000000000001', '2019-03-04T16:00:00', '2025-03-04T16:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId13}, 'H-LeftBrakeLight'   , 'XH4715'            , 'Left Brake Lights'         , 'OK'        , 'C-Level'     , 'OWN'     , 'PARTASPLANNED'    , 'production', 'Hella'          , '--', '4761635332437340935030188', 'BPNL000000000001', '2019-03-05T10:00:00', '2025-03-05T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId14}, 'H-RightBrakeLight'  , 'XH4716'            , 'Right Brake Lights'        , 'OK'        , 'C-Level'     , 'OWN'     , 'PARTASPLANNED'    , 'production', 'Hella'          , '--', '7258414561754525565313752', 'BPNL000000000001', '2019-03-05T16:00:00', '2025-03-05T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId15}, 'H-LeftFogLight'     , 'XH4717'            , 'Left Fog Lights'           , 'OK'        , 'C-Level'     , 'OWN'     , 'PARTASPLANNED'    , 'production', 'Hella'          , '--', '2039029959807211381794213', 'BPNL000000000001', '2019-03-08T10:00:00', '2025-03-08T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId16}, 'H-RightFogLight'    , 'XH4718'            , 'Right Fog Lights'          , 'OK'        , 'C-Level'     , 'OWN'     , 'PARTASPLANNED'    , 'production', 'Hella'          , '--', '7453407329689001534982316', 'BPNL000000000001', '2019-03-08T10:00:00', '2025-03-08T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId17}, 'H-LeftHighBeam'     , 'XH4719'            , 'Left High Beam'            , 'OK'        , 'C-Level'     , 'OWN'     , 'PARTASPLANNED'    , 'production', 'Hella'          , '--', '7675764145648317995475091', 'BPNL000000000001', '2019-03-30T10:00:00', '2025-03-30T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId18}, 'H-RightHighBeam'    , 'XH4720'            , 'Right High Beam'           , 'OK'        , 'C-Level'     , 'OWN'     , 'PARTASPLANNED'    , 'production', 'Hella'          , '--', '3706347239409133257103018', 'BPNL000000000001', '2019-03-31T10:00:00', '2025-03-31T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId19}, 'O-TurningLight'     , 'OTLB1'             , 'Osram Turning Light Bulb'  , 'OK'        , 'C-Level'     , 'SUPPLIER', 'PARTASPLANNED'    , 'production', 'Osram'          , '--', '2085860340477158419407610', 'BPNL000SUPPLIER1', '2019-02-10T10:00:00', '2025-02-10T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId20}, 'O-BrakeLight'       , 'OBLB1'             , 'Osram Brake Light Bulb'    , 'OK'        , 'C-Level'     , 'SUPPLIER', 'PARTASPLANNED'    , 'production', 'Osram'          , '--', '9067830022928159098792041', 'BPNL000SUPPLIER1', '2019-02-10T10:00:00', '2025-02-10T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId21}, 'P-TurningLight'     , 'PTLB1'             , 'Philips Turning Light Bulb', 'OK'        , 'C-Level'     , 'SUPPLIER', 'PARTASPLANNED'    , 'production', 'Philips'        , '--', '0756674361050961274380060', 'BPNL000SUPPLIER2', '2019-02-04T10:00:00', '2025-02-04T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId22}, 'P-BrakeLight'       , 'PBLB1'             , 'Philips Brake Light Bulb'  , 'OK'        , 'C-Level'     , 'SUPPLIER', 'PARTASPLANNED'    , 'production', 'Philips'        , '--', '6577291375491729147886674', 'BPNL000SUPPLIER2', '2019-02-04T16:00:00', '2025-02-04T16:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId23}, 'W-FogLight'         , 'WFLB1'             , 'Würth Fog Light Bulb'      , 'OK'        , 'C-Level'     , 'SUPPLIER', 'PARTASPLANNED'    , 'production', 'Würth'          , '--', '0662207279882485880581971', 'BPNL000SUPPLIER3', '2019-02-05T10:00:00', '2025-02-05T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId24}, 'W-HighBeam'         , 'WHBB1'             , 'Würth High Beam Bulb'      , 'OK'        , 'C-Level'     , 'SUPPLIER', 'PARTASPLANNED'    , 'production', 'Würth'          , '--', '0468074420786359082535855', 'BPNL000SUPPLIER3', '2019-02-05T16:00:00', '2025-02-05T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId25}, 'BMW-X1'             , 'X1ABC'             , 'X1'                        , 'OK'        , 'C-Level'     , 'CUSTOMER', 'PARTASPLANNED'    , 'production', 'BMW AG'         , '--', '2909308002296183400800584', 'BPNL000CUSTOMER1', '2019-08-08T10:00:00', '2025-08-08T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId26}, 'BMW-X3'             , 'X3ABC'             , 'X3'                        , 'OK'        , 'C-Level'     , 'CUSTOMER', 'PARTASPLANNED'    , 'production', 'BMW AG'         , '--', '0926935142628387651207558', 'BPNL000CUSTOMER1', '2019-08-08T10:00:00', '2025-08-08T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId27}, 'Audi-Q7'            , 'Q7XXX'             , 'Q7'                        , 'OK'        , 'C-Level'     , 'CUSTOMER', 'PARTASPLANNED'    , 'production', 'Audi AG'        , '--', '4892854584899578888039556', 'BPNL000CUSTOMER2', '2019-08-30T10:00:00', '2025-08-30T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId28}, 'Audi-Q8'            , 'Q8XXX'             , 'Q8'                        , 'OK'        , 'C-Level'     , 'CUSTOMER', 'PARTASPLANNED'    , 'production', 'Audi AG'        , '--', '2846299600736237305284278', 'BPNL000CUSTOMER2', '2019-08-31T10:00:00', '2025-08-31T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId29}, 'VW-ID.4'            , 'ID4YZ'             , 'ID.4'                      , 'OK'        , 'C-Level'     , 'CUSTOMER', 'PARTASPLANNED'    , 'production', 'VW AG'          , '--', '2712617812070054607743714', 'BPNL000CUSTOMER3', '2019-08-10T10:00:00', '2025-08-10T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId30}, 'VW-ID.5'            , 'ID5YZ'             , 'ID.5'                      , 'OK'        , 'C-Level'     , 'CUSTOMER', 'PARTASPLANNED'    , 'production', 'VW AG'          , '--', '0728960475743273447132197', 'BPNL000CUSTOMER3', '2019-08-10T10:00:00', '2025-08-10T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00');
