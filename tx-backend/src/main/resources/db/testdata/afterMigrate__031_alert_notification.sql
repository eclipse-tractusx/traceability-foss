-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into alert_notification
    (id                     , contract_agreement_id, edc_url                                                 , notification_reference_id, created_by        , send_to           , alert_id   , target_date                           , severity, created_by_name, send_to_name, edc_notification_id, status, created                              , updated, message_id, is_initial)
values
    (${alertNotificationId1}, 1                    , 'https://some.edc.url'                                  , 1                        , 'BPNL000SUPPLIER1', 'BPNL000000000001', ${alertId1}, current_timestamp + interval '1 Month', 1       , 'Osram'        , 'Hella'     , 1                  , '2'   , current_timestamp - interval '1 days', null   , 1         , true),
    (${alertNotificationId2}, 1                    , 'https://some.edc.url'                                  , 1                        , 'BPNL000SUPPLIER2', 'BPNL000000000001', ${alertId2}, current_timestamp + interval '1 Month', 1       , 'Phillips'     , 'Hella'     , 1                  , '3'   , current_timestamp                    , null   , 1         , true),
    (${alertNotificationId3}, 1                    , 'https://some.edc.url'                                  , 1                        , 'BPNL000000000001', 'BPNL000CUSTOMER1', ${alertId3}, current_timestamp + interval '1 Month', 1       , 'Hella'        , 'BMW AG'    , 1                  , '0'   , current_timestamp                    , null   , 1         , true),
    (${alertNotificationId4}, 'contractAgreementId', 'http://localhost:8082/api/qualitynotifications/receive', 1                        , 'BPNL000000000001', 'BPNL000CUSTOMER2', ${alertId4}, current_timestamp + interval '1 Month', 1       , 'Hella'        , 'Audi AG'   , 1                  , '1'   , current_timestamp                    , null   , 1         , true);
