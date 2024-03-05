-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state CLOSED in Severity Critical for asBuilt asset Philips P-BrakeLight which is sent from BPNL000SUPPLIER2 to BPNL000000000001

---
insert into alert
    (id                 , bpn            , close_reason             , created                              , description               , status  , side      , accept_reason, decline_reason, updated, error_message)
values
    (${alertReceivedId5}, ${bpnSupplier2}, 'Issue has been resolved', current_timestamp - interval '6 days', 'Alert about P-BrakeLight', 'CLOSED', 'RECEIVER', null         , null          , null   , null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq1', (select max(a.id) from alert a), true);

---
-- initial message
insert into alert_notification
    (id                              , alert_id           , contract_agreement_id, edc_url, notification_reference_id             , created_by     , send_to  , target_date                           , severity  , created_by_name    , send_to_name , edc_notification_id                   , status  , created                              , updated          , message_id                            , is_initial)
values
    (${alertNotificationReceivedId5a}, ${alertReceivedId5}, null                 , null   , '9c1290e1-7292-42fc-aef3-16f384e0bc76', ${bpnSupplier2}, ${bpnOwn}, current_timestamp + interval '1 month', 'CRITICAL', ${bpnSupplier2Name}, ${bpnOwnName}, '01d08ee0-ee35-48ca-90ae-e4eea05dba92', 'CLOSED', current_timestamp - interval '5 days', current_timestamp, '68ebeb5f-158d-480e-b466-24304842c22c', false);

---
-- join initial notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id           , asset_id)
values
    (${alertNotificationReceivedId5a}, ${assetAsBuiltId22});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id           , asset_id)
values
    (${alertReceivedId5}, ${assetAsBuiltId22});

---
---
-- CLOSED by sender notification message
insert into alert_notification
    (id                              , alert_id           , contract_agreement_id, edc_url                    , notification_reference_id             , created_by     , send_to  , target_date                           , severity  , created_by_name    , send_to_name , edc_notification_id                   , status  , created                                , updated                                , message_id, is_initial)
values
    (${alertNotificationReceivedId5b}, ${alertReceivedId5}, 'contractAgreementId', 'http://localhost:5001/edc', 'cc5378bd-d769-41cc-b528-dcc0e37b230a', ${bpnSupplier2}, ${bpnOwn}, current_timestamp + interval '1 month', 'CRITICAL', ${bpnSupplier2Name}, ${bpnOwnName}, '4981b000-2da0-4e3c-9088-da62c8d475be', 'CLOSED', current_timestamp - interval '12 hours', current_timestamp - interval '12 hours', null      , false);

---
-- join CLOSED notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id           , asset_id)
values
    (${alertNotificationReceivedId5b}, ${assetAsBuiltId22});
