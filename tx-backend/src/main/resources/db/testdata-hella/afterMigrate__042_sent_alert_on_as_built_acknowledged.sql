-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state ACKNOWLEDGED in Severity Life-threatening for asBuilt asset Brake lights left which is sent from BPNL000000000001 to BPNL000CUSTOMER2

---
insert into alert
    (id             , bpn      , close_reason, created                             , description                    , status        , side    , accept_reason, decline_reason, updated, error_message)
values
    (${alertSentId3}, ${bpnOwn}, null        , current_timestamp - interval '1 day', 'Alert about Brake lights left', 'ACKNOWLEDGED', 'SENDER', null         , null          , null   , null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq1', (select max(a.id) from alert a), true);

---
-- initial message
insert into alert_notification
    (id                          , alert_id       , contract_agreement_id, edc_url, notification_reference_id             , created_by, send_to        , target_date, severity          , created_by_name, send_to_name       , edc_notification_id                   , status   , created                             , updated          , message_id, is_initial)
values
    (${alertNotificationSentId3a}, ${alertSentId3}, 'contractAgreementId', null   , '7354895f-9e79-4582-af35-7d5b820ac9b6', ${bpnOwn} , ${bpnCustomer2}, null       , 'LIFE_THREATENING', ${bpnOwnName}  , ${bpnCustomer2Name}, '7354895f-9e79-4582-af35-7d5b820ac9b6', 'CREATED', current_timestamp - interval '1 day', current_timestamp, null      , true);

---
-- join initial notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id       , asset_id)
values
    (${alertNotificationSentId3a}, ${assetAsBuiltId13});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id       , asset_id)
values
    (${alertSentId3}, ${assetAsBuiltId13});

---
-- ACK by receiver notification message
insert into alert_notification
    (id                          , alert_id       , contract_agreement_id, edc_url, notification_reference_id             , created_by     , send_to  , target_date, severity          , created_by_name    , send_to_name , edc_notification_id                   , status        , created                                , updated                                , message_id, is_initial)
values
    (${alertNotificationSentId3b}, ${alertSentId3}, 'contractAgreementId', null   , '7354895f-9e79-4582-af35-7d5b820ac9b6', ${bpnCustomer2}, ${bpnOwn}, null       , 'LIFE_THREATENING', ${bpnCustomer2Name}, ${bpnOwnName}, '7354895f-9e79-4582-af35-7d5b820ac9b6', 'ACKNOWLEDGED', current_timestamp - interval '12 hours', current_timestamp - interval '12 hours', null      , false);

---
-- join ACK notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id       , asset_id)
values
    (${alertNotificationSentId3b}, ${assetAsBuiltId13});
