-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into investigation_notification
    (id                             , contract_agreement_id, edc_url, notification_reference_id, send_to           , created_by        , investigation_id   , target_date          , severity, created_by_name  , send_to_name  , edc_notification_id, status, created              , updated, message_id, is_initial)
values
    (${investigationNotificationId1}, 1                    , null   , 1                        , 'BPNL000000000001', 'created_by'      , ${investigationId1}, '2023-12-04T13:48:54', 1       , 'created_by_name', 'send_to_name', 1                  , '2'   , '2023-10-04T13:48:54', null   , 1         , true),
    (${investigationNotificationId2}, 1                    , null   , 1                        , 'BPNL000000000001', 'created_by'      , ${investigationId2}, '2023-12-04T13:48:54', 1       , 'created_by_name', 'send_to_name', 1                  , '3'   , '2023-10-04T13:48:54', null   , 1         , true),
    (${investigationNotificationId3}, 1                    , null   , 1                        , 'send_to'         , 'BPNL000000000001', ${investigationId3}, '2023-12-04T13:48:54', 1       , 'created_by_name', 'send_to_name', 1                  , '0'   , '2023-10-04T13:48:54', null   , 1         , true),
    (${investigationNotificationId4}, 1                    , null   , 1                        , 'send_to'         , 'BPNL000000000001', ${investigationId4}, '2023-12-04T13:48:54', 1       , 'created_by_name', 'send_to_name', 1                  , '1'   , '2023-10-04T13:48:54', null   , 1         , true);
