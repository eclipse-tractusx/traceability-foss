-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state CANCELED in Severity Minor for asBuilt asset Fog lights left which is sent from BPNL000000000001 to BPNL000CUSTOMER1

---
insert into alert
    (id             , bpn      , close_reason, created                              , description                            , status    , side    , accept_reason, decline_reason, updated          , error_message)
values
    (${alertSentId6}, ${bpnOwn}, null        , current_timestamp - interval '3 days', 'Cancelled Alert about Fog lights left', 'CANCELED', 'SENDER', null         , null          , current_timestamp, null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq1', (select max(a.id) from alert a), true);

---
-- initial message
insert into alert_notification
    (id                         , alert_id       , contract_agreement_id, edc_url                    , notification_reference_id, created_by, send_to        , target_date                           , severity, created_by_name, send_to_name       , edc_notification_id        , status    , created                              , updated          , message_id                            , is_initial)
values
    (${alertNotificationSentId6}, ${alertSentId6}, 'contractAgreementId', 'http://localhost:5001/edc', 'null'                   , ${bpnOwn} , ${bpnCustomer1}, current_timestamp + interval '1 month', 'MINOR' , ${bpnOwnName}  , ${bpnCustomer1Name}, ${alertNotificationSentId6}, 'CANCELED', current_timestamp - interval '3 days', current_timestamp, 'bd6ca75b-9d1c-44bd-bc80-b3afca317daf', true);

---
-- join initial notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id      , asset_id)
values
    (${alertNotificationSentId6}, ${assetAsBuiltId15});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id       , asset_id)
values
    (${alertSentId6}, ${assetAsBuiltId15});
