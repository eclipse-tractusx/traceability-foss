-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state DECLINED in Severity Major for asBuilt asset High beam left which is sent from BPNL000000000001 to BPNL000CUSTOMER3

---
insert into alert
    (id             , bpn      , close_reason, created                              , description                 , status    , side    , accept_reason, decline_reason                       , updated, error_message)
values
    (${alertSentId5}, ${bpnOwn}, null        , current_timestamp - interval '4 days', 'Alert about High beam left', 'DECLINED', 'SENDER', null         , 'Thanks, but this doesn''t affect us', null   , null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq1', (select max(a.id) from alert a), true);

---
-- initial message
insert into alert_notification
    (id                          , alert_id       , contract_agreement_id, edc_url, notification_reference_id, created_by, send_to        , target_date                           , severity, created_by_name, send_to_name       , edc_notification_id                   , status   , created                              , updated          , message_id, is_initial)
values
    (${alertNotificationSentId5a}, ${alertSentId5}, null                 , null   , 'null'                   , ${bpnOwn} , ${bpnCustomer3}, current_timestamp + interval '2 weeks', 'MAJOR' , ${bpnOwnName}  , ${bpnCustomer3Name}, 'fe40ca1c-0d4d-4af0-9ccc-415113f4b7a1', 'CREATED', current_timestamp - interval '4 days', current_timestamp, null      , true);

---
-- join initial notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id       , asset_id)
values
    (${alertNotificationSentId5a}, ${assetAsBuiltId17});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id       , asset_id)
values
    (${alertSentId5}, ${assetAsBuiltId17});

---
-- DECLINED by receiver notification message
insert into alert_notification
    (id                          , alert_id       , contract_agreement_id, edc_url, notification_reference_id             , created_by     , send_to  , target_date                           , severity, created_by_name    , send_to_name , edc_notification_id                   , status    , created                              , updated                                , message_id, is_initial)
values
    (${alertNotificationSentId5b}, ${alertSentId5}, null                 , null   , 'fe40ca1c-0d4d-4af0-9ccc-415113f4b7a1', ${bpnCustomer3}, ${bpnOwn}, current_timestamp + interval '2 weeks', 'MAJOR' , ${bpnCustomer3Name}, ${bpnOwnName}, 'fe40ca1c-0d4d-4af0-9ccc-415113f4b7a1', 'DECLINED', current_timestamp - interval '2 days', current_timestamp - interval '12 hours', null      , false);

---
-- join DECLINED notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id       , asset_id)
values
    (${alertNotificationSentId5b}, ${assetAsBuiltId17});
