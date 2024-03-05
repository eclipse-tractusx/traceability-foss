-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state ACCEPTED in Severity Minor for asBuilt asset Philips P-TurningLight which is sent from BPNL000SUPPLIER2 to BPNL000000000001

---
insert into alert
    (id                 , bpn            , close_reason, created                              , description                 , status    , side      , accept_reason           , decline_reason, updated, error_message)
values
    (${alertReceivedId3}, ${bpnSupplier2}, null        , current_timestamp - interval '4 days', 'Alert about P-TurningLight', 'ACCEPTED', 'RECEIVER', 'Part has been replaced', null          , null   , null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq1', (select max(a.id) from alert a), true);

---
-- initial message
insert into alert_notification
    (id                              , alert_id           , contract_agreement_id, edc_url, notification_reference_id             , created_by     , send_to  , target_date                           , severity, created_by_name    , send_to_name , edc_notification_id                   , status    , created                              , updated          , message_id                            , is_initial)
values
    (${alertNotificationReceivedId3a}, ${alertReceivedId3}, null                 , null   , 'be320b81-01ab-46fd-baf5-4e182978c31e', ${bpnSupplier2}, ${bpnOwn}, current_timestamp + interval '1 month', 'MINOR' , ${bpnSupplier2Name}, ${bpnOwnName}, 'c7968bc9-22d0-48d8-8269-10a9043fcf47', 'RECEIVED', current_timestamp - interval '3 days', current_timestamp, '68ebeb5f-158d-480e-b466-24304842c22c', false);

---
-- join initial notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id           , asset_id)
values
    (${alertNotificationReceivedId3a}, ${assetAsBuiltId21});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id           , asset_id)
values
    (${alertReceivedId3}, ${assetAsBuiltId21});

---
---
-- ACCEPTED by receiver notification message
insert into alert_notification
    (id                              , alert_id           , contract_agreement_id, edc_url                    , notification_reference_id             , created_by, send_to        , target_date                           , severity, created_by_name, send_to_name       , edc_notification_id                   , status    , created                                , updated                                , message_id, is_initial)
values
    (${alertNotificationReceivedId3b}, ${alertReceivedId3}, 'contractAgreementId', 'http://localhost:5001/edc', '6df59246-9127-430c-9135-bbb6741fe734', ${bpnOwn} , ${bpnSupplier2}, current_timestamp + interval '1 month', 'MINOR' , ${bpnOwnName}  , ${bpnSupplier2Name}, 'a813eba6-9f11-4079-95ba-46aaa8257761', 'ACCEPTED', current_timestamp - interval '12 hours', current_timestamp - interval '12 hours', null      , false);

---
-- join ACCEPTED notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id           , asset_id)
values
    (${alertNotificationReceivedId3b}, ${assetAsBuiltId21});
