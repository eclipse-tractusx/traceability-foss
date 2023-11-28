-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state CREATED with Severity Minor for asBuilt asset Left Headlights which is sent from BPNL000000000001 to BPNL000CUSTOMER1

---
insert into alert
    (id         , bpn                , close_reason, created          , description                  , status   , side    , accept_reason, decline_reason, updated, error_message)
values
    (${alertSentId1}, 'BPNL000000000001', null        , current_timestamp, 'Second Alert about Left Headlights', 'CREATED', 'SENDER', null         , null          , null   , null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq', (select max(a.id) from alert a), true);

---
-- initial message
insert into alert_notification
    (id                     , alert_id   , contract_agreement_id, edc_url, notification_reference_id, created_by        , send_to           , target_date                           , severity, created_by_name, send_to_name, edc_notification_id    , status, created                                 , updated          , message_id                            , is_initial)
values
    (${alertNotificationSentId1}, ${alertSentId1}, null                 , null   , 'null'                   , 'BPNL000000000001', 'BPNL000CUSTOMER1', current_timestamp + interval '1 month', 0       , 'Hella'        , 'BMW AG'    , ${alertNotificationSentId1}, 0     , current_timestamp - interval '1 seconds', current_timestamp, '42e28782-bf4c-45a2-82b7-1757aa4b8772', true);

---
-- join initial notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id  , asset_id)
values
    (${alertNotificationSentId1}, ${assetAsBuiltId01});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id   , asset_id)
values
    (${alertSentId1}, ${assetAsBuiltId01});

---
update assets_as_built
    set active_alert = true
    where id in (${assetAsBuiltId01});
