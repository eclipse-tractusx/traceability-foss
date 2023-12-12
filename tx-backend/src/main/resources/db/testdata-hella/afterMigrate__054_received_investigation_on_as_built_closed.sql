-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state CLOSED in Severity Minor for asBuilt asset H-LeftFogLight which is sent from BPNL000CUSTOMER3 to BPNL000000000001

---
insert into investigation
    (id                         , bpn            , close_reason                                  , created                              , description                                          , status  , side      , accept_reason, decline_reason, updated                                , error_message)
values
    (${investigationReceivedId5}, ${bpnCustomer3}, 'We confirm that the problem has been fixed.' , current_timestamp - interval '5 days', 'Investigation on H-LeftFogLight due to malfunction.', 'CLOSED', 'RECEIVER', null         , null           , current_timestamp - interval '1 hours', null);

---
-- reset sequence to highest next-val
select setval('investigation_id_seq', (select max(i.id) from investigation i), true);

---
insert into investigation_notification
    (id                                      , contract_agreement_id, edc_url, notification_reference_id             , created_by     , send_to  , investigation_id           , target_date                           , severity, created_by_name, send_to_name, edc_notification_id                   , status, created                              , updated                               , message_id                            , is_initial)
values
    (${investigationNotificationReceivedId5a}, null                 , null   , '8925f21f-09eb-4789-81fb-ec221e9e1561', ${bpnCustomer3}, ${bpnOwn}, ${investigationReceivedId5}, current_timestamp + interval '1 month', 0       , 'VW AG'        , 'Hella'     , '8925f21f-09eb-4789-81fb-ec221e9e1561', 7     , current_timestamp - interval '5 days', current_timestamp - interval '2 hours', 'e04f75e8-d37b-42e4-8cf7-6127f35f3ed5', false);

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                         , asset_id)
values
    (${investigationNotificationReceivedId5a}, ${assetAsBuiltId15});

---
-- join investigation to asset
insert into assets_as_built_investigations
    (investigation_id           , asset_id)
values
    (${investigationReceivedId5}, ${assetAsBuiltId15});

---
update assets_as_built
    set in_investigation = false
    where id in (${assetAsBuiltId15});

---
---
-- CLOSED by sender notification message
insert into investigation_notification
    (id                                      , contract_agreement_id, edc_url                                                 , notification_reference_id, created_by     , send_to  , investigation_id           , target_date                           , severity, created_by_name, send_to_name, edc_notification_id                   , status, created                               , updated                              , message_id                            , is_initial)
values
    (${investigationNotificationReceivedId5b}, 'contractAgreementId', 'http://localhost:8082/api/qualitynotifications/receive', null                     , ${bpnCustomer3}, ${bpnOwn}, ${investigationReceivedId5}, current_timestamp + interval '1 month', 0       , 'VW AG'        ,'Hella'      , '8925f21f-09eb-4789-81fb-ec221e9e1561', 7     , current_timestamp - interval '5 days ', current_timestamp - interval '1 hour', '207ba6cf-217b-401d-a5da-69cac8b154a5', false);

---
-- join CLOSED notification to asset
insert into assets_as_built_notifications
    (notification_id                         , asset_id)
values
    (${investigationNotificationReceivedId5b}, ${assetAsBuiltId15});
