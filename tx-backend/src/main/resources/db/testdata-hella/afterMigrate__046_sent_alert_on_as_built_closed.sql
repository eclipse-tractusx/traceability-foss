-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state CLOSED in Severity Critical for asBuilt asset Fog lights left which is sent from BPNL000000000001 to BPNL000CUSTOMER1

---
insert into alert
    (id             , bpn      , close_reason            , created                              , description                         , status  , side    , accept_reason, decline_reason, updated          , error_message)
values
    (${alertSentId7}, ${bpnOwn}, 'This was a false alarm', current_timestamp - interval '2 days', 'Closed Alert about Fog lights left', 'CLOSED', 'SENDER', null         , null          , current_timestamp, null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq1', (select max(a.id) from alert a), true);

---
-- initial message
insert into alert_notification
    (id                          , alert_id       , contract_agreement_id, edc_url                    , notification_reference_id, created_by, send_to        , target_date                           , severity  , created_by_name, send_to_name       , edc_notification_id         , status  , created                              , updated          , message_id                            , is_initial)
values
    (${alertNotificationSentId7a}, ${alertSentId7}, 'contractAgreementId', 'http://localhost:5001/edc', 'null'                   , ${bpnOwn} , ${bpnCustomer1}, current_timestamp + interval '1 month', 'CRITICAL', ${bpnOwnName}  , ${bpnCustomer1Name}, ${alertNotificationSentId7a}, 'CLOSED', current_timestamp - interval '2 days', current_timestamp, '3ca2e271-bffb-4e34-83c5-13fc1c462062', true);

---
-- join initial notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id       , asset_id)
values
    (${alertNotificationSentId7a}, ${assetAsBuiltId15});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id       , asset_id)
values
    (${alertSentId7}, ${assetAsBuiltId15});

---
-- CLOSED by sender notification message
insert into alert_notification
    (id                          , alert_id       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by, send_to        , target_date                           , severity  , created_by_name, send_to_name       , edc_notification_id                   , status  , created                              , updated                                , message_id, is_initial)
values
    (${alertNotificationSentId7b}, ${alertSentId7}, 'contractAgreementId', 'http://localhost:5001/edc', '04d9d9b0-26cc-4209-a039-825f43cca44e', ${bpnOwn} , ${bpnCustomer1}, current_timestamp + interval '2 weeks', 'CRITICAL', ${bpnOwnName}  , ${bpnCustomer1Name}, '6eb2499c-6ad0-426d-96d9-a723fab7da7b', 'CLOSED', current_timestamp - interval '2 days', current_timestamp - interval '12 hours', null      , false);

---
-- join CLOSED notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id       , asset_id)
values
    (${alertNotificationSentId7b}, ${assetAsBuiltId15});
