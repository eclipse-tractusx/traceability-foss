-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state ACCEPTED in Severity Minor for asBuilt asset Turning lights left which is sent from BPNL000000000001 to BPNL000CUSTOMER2

---
insert into alert
    (id             , bpn      , close_reason, created                              , description                      , status    , side    , accept_reason                             , decline_reason, updated, error_message)
values
    (${alertSentId4}, ${bpnOwn}, null        , current_timestamp - interval '5 days', 'Alert about Turning lights left', 'ACCEPTED', 'SENDER', 'Thanks for letting us know. We''ll check', null          , null   , null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq1', (select max(a.id) from alert a), true);

---
-- initial message
insert into alert_notification
    (id                          , alert_id       , contract_agreement_id, edc_url, notification_reference_id             , created_by, send_to        , target_date                           , severity, created_by_name, send_to_name       , edc_notification_id                   , status   , created                              , updated          , message_id, is_initial)
values
    (${alertNotificationSentId4a}, ${alertSentId4}, 'contractAgreementId', null   , '8e777e64-f7f3-44e2-b78e-75da964f1531', ${bpnOwn} , ${bpnCustomer2}, current_timestamp + interval '1 month', 'MINOR' , ${bpnOwnName}  , ${bpnCustomer2Name}, '8e777e64-f7f3-44e2-b78e-75da964f1531', 'CREATED', current_timestamp - interval '5 days', current_timestamp, null      , true);

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
-- ACCEPTED by receiver notification message
insert into alert_notification
    (id                          , alert_id       , contract_agreement_id, edc_url, notification_reference_id             , created_by     , send_to  , target_date                           , severity, created_by_name    , send_to_name , edc_notification_id                   , status    , created                              , updated          , message_id, is_initial)
values
    (${alertNotificationSentId4b}, ${alertSentId4}, 'contractAgreementId', null   , '8e777e64-f7f3-44e2-b78e-75da964f1531', ${bpnCustomer2}, ${bpnOwn}, current_timestamp + interval '1 month', 'MINOR' , ${bpnCustomer2Name}, ${bpnOwnName}, '8e777e64-f7f3-44e2-b78e-75da964f1531', 'ACCEPTED', current_timestamp - interval '3 days', current_timestamp, null      , false);

---
-- join ACCEPTED notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id       , asset_id)
values
    (${alertNotificationSentId4b}, ${assetAsBuiltId13});
