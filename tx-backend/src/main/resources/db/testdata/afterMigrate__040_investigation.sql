-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into investigation
    (id                             , bpn                                    , close_reason, created              , description           , status        , side      , accept_reason, decline_reason, updated, error_message)
values
    (${investigationId1}            , 'BPNL000CUSTOMER1'                     , null        , '2023-10-04T13:48:54', 'Test Investigation 1', 'RECEIVED'    , 'RECEIVER', null         , null          , null   , null),
    (${investigationId2}            , 'BPNL000CUSTOMER1'                     , null        , '2023-10-04T13:48:54', 'Test Investigation 2', 'ACKNOWLEDGED', 'RECEIVER', null         , null          , null   , null),
    (${investigationId3}            , 'BPNL000000000001'                     , null        , '2023-10-04T13:48:54', 'Test Investigation 3', 'CREATED'     , 'SENDER'  , null         , null          , null   , null),
    (${investigationId4}            , 'BPNL000000000001'                     , null        , '2023-10-04T13:48:54', 'Test Investigation 4', 'SENT'        , 'SENDER'  , null         , null          , null   , null);

select setval('investigation_id_seq', (select max(i.id) from investigation i), true);
