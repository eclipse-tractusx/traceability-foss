-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state SENT with Severity Major for asBuilt asset Xenon Left-Headlights which is sent from BPNL000000000001 to BPNL000CUSTOMER1

---
insert into alert
    (id         , bpn                , close_reason, created          , description                  , status, side    , accept_reason, decline_reason, updated          , error_message)
values
    (${alertSentId2}, 'BPNL000000000001', null        , current_timestamp - interval '1 day', 'First Alert about Left Headlights', 'SENT', 'SENDER', null         , null          , current_timestamp, null);

---
-- reset sequence to highest next-val
select setval('alert_id_seq', (select max(a.id) from alert a), true);

---
-- initial message
insert into alert_notification
    (id                     , alert_id   , contract_agreement_id, edc_url                                                 , notification_reference_id, created_by        , send_to           , target_date                           , severity, created_by_name, send_to_name, edc_notification_id    , status, created                                 , updated          , message_id                            , is_initial)
values
    (${alertNotificationSentId2}, ${alertSentId2}, 'contractAgreementId', 'http://localhost:8082/api/qualitynotifications/receive', 'null'                   , 'BPNL000000000001', 'BPNL000CUSTOMER1', current_timestamp + interval '1 week', 1       , 'Hella'        , 'BMW AG'    , ${alertNotificationSentId2}, 0     , current_timestamp - interval '1 day', current_timestamp, '2cf84b7c-5e42-46f2-8869-12b053b9a276', true);

---
-- join initial notification to asset
insert into asset_as_built_alert_notifications
    (alert_notification_id  , asset_id)
values
    (${alertNotificationSentId2}, ${assetAsBuiltId01});

---
-- join alert to asset
insert into assets_as_built_alerts
    (alert_id   , asset_id)
values
    (${alertSentId2}, ${assetAsBuiltId01});

---
update assets_as_built
    set active_alert = true
    where id in (${assetAsBuiltId01});