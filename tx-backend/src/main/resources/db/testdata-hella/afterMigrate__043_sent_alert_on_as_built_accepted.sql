-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state ACCEPTED with Severity Minor for asBuilt asset Turning lights left which is sent from BPNL000000000001 to BPNL000CUSTOMER2

---
insert into alert
    (id             , bpn      , close_reason, created                              , description                      , status    , side    , accept_reason                              , decline_reason, updated          , error_message)
values
    (${alertSentId4}, ${bpnOwn}, null        , current_timestamp - interval '5 days', 'Alert about Turning lights left', 'ACCEPTED', 'SENDER', 'Thanks for letting us know. We''ll check' , null          , current_timestamp, null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq', (select max(a.id) from alert a), true);

---
-- initial message
insert into alert_notification
    (id                          , alert_id       , contract_agreement_id, edc_url                                                 , notification_reference_id, created_by, send_to        , target_date                           , severity, created_by_name, send_to_name, edc_notification_id          , status, created                              , updated          , message_id                            , is_initial)
values
    (${alertNotificationSentId4a}, ${alertSentId4}, 'contractAgreementId', 'http://localhost:8082/api/qualitynotifications/receive', 'null'                   , ${bpnOwn} , ${bpnCustomer2}, current_timestamp + interval '1 month', 0       , 'Hella'        , 'Audi AG'    , ${alertNotificationSentId4a}, 0     , current_timestamp - interval '5 days', current_timestamp, '2cf84b7c-5e42-46f2-8869-12b053b9a276', true);

---
-- join initial notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id       , asset_id)
values
    (${alertNotificationSentId4a}, ${assetAsBuiltId13});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id       , asset_id)
values
    (${alertSentId4}, ${assetAsBuiltId13});

---
update assets_as_built
    set active_alert = true
    where id in (${assetAsBuiltId13});

---
-- ACCEPTED by receiver notification message
insert into alert_notification
    (id                          , alert_id       , contract_agreement_id, edc_url                                                 , notification_reference_id             , created_by     , send_to  , target_date                           , severity, created_by_name, send_to_name, edc_notification_id                   , status, created                              , updated          , message_id                            , is_initial)
values
    (${alertNotificationSentId4b}, ${alertSentId4}, 'contractAgreementId', 'http://localhost:8082/api/qualitynotifications/receive', 'cc49777f-3c8b-47d6-b1cf-f51783737292', ${bpnCustomer2}, ${bpnOwn}, current_timestamp + interval '1 month', 0       , 'Audi AG'      , 'Hella'     , 'cc49777f-3c8b-47d6-b1cf-f51783737292', 4     , current_timestamp - interval '3 days', current_timestamp, 'f305046d-333a-4d44-ba3e-9a4ef1337ba6', false);

---
-- join ACCEPTED notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id       , asset_id)
values
    (${alertNotificationSentId4b}, ${assetAsBuiltId13});
