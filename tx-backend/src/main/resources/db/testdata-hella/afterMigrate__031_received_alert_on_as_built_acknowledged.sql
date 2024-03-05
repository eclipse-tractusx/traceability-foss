-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state ACKNOWLEDGED in Severity Life-threatening for asBuilt asset Philips Front Right-D3H which is sent from BPNL000SUPPLIER2 to BPNL000000000001

---
insert into alert
    (id                 , bpn            , close_reason, created                              , description                      , status        , side      , accept_reason, decline_reason, updated, error_message)
values
    (${alertReceivedId2}, ${bpnSupplier2}, null        , current_timestamp - interval '3 days', 'Alert about Right P Light Bulbs', 'ACKNOWLEDGED', 'RECEIVER', null         , null          , null   , null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq1', (select max(a.id) from alert a), true);

---
-- initial message
insert into alert_notification
    (id                              , alert_id           , contract_agreement_id, edc_url, notification_reference_id             , created_by     , send_to  , target_date                           , severity          , created_by_name    , send_to_name , edc_notification_id                   , status    , created                              , updated          , message_id, is_initial)
values
    (${alertNotificationReceivedId2a}, ${alertReceivedId2}, null                 , null   , '403818ab-28fc-428f-99b1-61077fede954', ${bpnSupplier2}, ${bpnOwn}, current_timestamp + interval '1 month', 'LIFE_THREATENING', ${bpnSupplier2Name}, ${bpnOwnName}, '403818ab-28fc-428f-99b1-61077fede954', 'RECEIVED', current_timestamp - interval '2 days', current_timestamp, null      , false);

---
-- join initial notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id           , asset_id)
values
    (${alertNotificationReceivedId2a}, ${assetAsBuiltId06});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id           , asset_id)
values
    (${alertReceivedId2}, ${assetAsBuiltId06});

---
---
-- ACK by receiver notification message
insert into alert_notification
    (id                              , alert_id           , contract_agreement_id, edc_url, notification_reference_id             , created_by, send_to        , target_date                           , severity          , created_by_name, send_to_name       , edc_notification_id                   , status        , created                                , updated                                , message_id, is_initial)
values
    (${alertNotificationReceivedId2b}, ${alertReceivedId2}, null                 , null   , '403818ab-28fc-428f-99b1-61077fede954', ${bpnOwn} , ${bpnSupplier2}, current_timestamp + interval '1 month', 'LIFE_THREATENING', ${bpnOwnName}  , ${bpnSupplier2Name}, '403818ab-28fc-428f-99b1-61077fede954', 'ACKNOWLEDGED', current_timestamp - interval '12 hours', current_timestamp - interval '12 hours', null      , false);

---
-- join ACK notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id           , asset_id)
values
    (${alertNotificationReceivedId2b}, ${assetAsBuiltId06});
