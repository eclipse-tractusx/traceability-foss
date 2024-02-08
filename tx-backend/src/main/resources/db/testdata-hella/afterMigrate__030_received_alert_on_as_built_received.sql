-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state RECEIVED in Severity Critical for asBuilt asset Osram Front Left-AX400 which is sent from BPNL000SUPPLIER1 to BPNL000000000001

---
insert into alert
    (id                 , bpn            , close_reason, created                              , description                     , status    , side      , accept_reason, decline_reason, updated, error_message)
values
    (${alertReceivedId1}, ${bpnSupplier1}, null        , current_timestamp - interval '2 days', 'Alert about Left O Light Bulbs', 'RECEIVED', 'RECEIVER', null         , null          , null   , null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq1', (select max(a.id) from alert a), true);

---
insert into alert_notification
    (id                             , alert_id           , contract_agreement_id, edc_url, notification_reference_id             , created_by     , send_to  , target_date                           , severity  , created_by_name, send_to_name, edc_notification_id                   , status    , created                             , updated, message_id                            , is_initial)
values
    (${alertNotificationReceivedId1}, ${alertReceivedId1}, null                 , null   , 'ded38f08-1b9c-497d-b994-6feba92b7f41', ${bpnSupplier1}, ${bpnOwn}, current_timestamp + interval '1 month', 'CRITICAL', 'Osram'        , 'Hella'     , 'ded38f08-1b9c-497d-b994-6feba92b7f41', 'RECEIVED', current_timestamp - interval '1 day', null   , '7d0891d2-4940-4802-b0bc-cc30f9e94e76', false);

---
-- join notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id          , asset_id)
values
    (${alertNotificationReceivedId1}, ${assetAsBuiltId03});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id           , asset_id)
values
    (${alertReceivedId1}, ${assetAsBuiltId03});
