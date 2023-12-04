-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state RECEIVED in Severity Critical for asBuilt asset Petersmeier K2367 which is sent from BPNL00SUPPLIER21 to BPNL000SUPPLIER1

---
insert into alert
    (id                 , bpn               , close_reason, created                              , description                              , status    , side      , accept_reason, decline_reason, updated, error_message)
values
    (${alertReceivedId1}, 'BPNL00SUPPLIER21', null        , current_timestamp - interval '2 days', 'Alert about Packaging for turning light', 'RECEIVED', 'RECEIVER', null         , null          , null   , null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq', (select max(a.id) from alert a), true);

---
insert into alert_notification
    (id                             , alert_id           , contract_agreement_id, edc_url, notification_reference_id             , created_by         , send_to           , target_date                           , severity, created_by_name, send_to_name, edc_notification_id                   , status, created                           , updated, message_id                            , is_initial)
values
    (${alertNotificationReceivedId1}, ${alertReceivedId1}, null                 , null   , 'ded38f08-1b9c-497d-b994-6feba92b7f41',  'BPNL00SUPPLIER21', 'BPNL000SUPPLIER1', current_timestamp + interval '1 month', 2       , 'Petersmeier'  , 'Osram'     , 'ded38f08-1b9c-497d-b994-6feba92b7f41', 2   , current_timestamp - interval '1 day', null   , '7d0891d2-4940-4802-b0bc-cc30f9e94e76', false);

---
-- join notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id          , asset_id)
values
    (${alertNotificationReceivedId1}, ${assetAsBuiltId10});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id           , asset_id)
values
    (${alertReceivedId1}, ${assetAsBuiltId10});

---
update assets_as_built
    set active_alert = true
    where id in (${assetAsBuiltId10});
