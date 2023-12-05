-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state CANCELED in Severity Life-threatening for asBuilt asset Osram O-BrakeLight which is sent from BPNL000000000001 to BPNL000SUPPLIER1

---
insert into investigation
    (id                     , bpn      , close_reason , created                               , description                                               , status    , side    , accept_reason , decline_reason     , updated                              , error_message)
values
    (${investigationSentId6}, ${bpnOwn}, null         , current_timestamp - interval '7 days', 'Investigation on Osram O-BrakeLight due to strange noise.', 'CANCELED', 'SENDER', null          , null             , current_timestamp - interval '1 hour', null);

---
-- reset sequence to highest next-val
select setval('investigation_id_seq', (select max(i.id) from investigation i), true);

---
insert into investigation_notification
    (id                                 , contract_agreement_id, edc_url                                                 , notification_reference_id, send_to        , created_by, investigation_id       , target_date                          , severity, created_by_name, send_to_name , edc_notification_id                   , status, created                               , updated                              , message_id                            , is_initial)
values
    (${investigationNotificationSentId6}, 'contractAgreementId', 'http://localhost:8082/api/qualitynotifications/receive', null                     , ${bpnSupplier1}, ${bpnOwn} , ${investigationSentId6}, current_timestamp + interval '2 days', 3       , 'Hella'        , 'Osram'      , '3ac2239a-e63f-4c19-b3b3-e6a2e5a240da', 7     , current_timestamp - interval '7 days', current_timestamp - interval '1 hour', '749b31e9-9e73-4699-9470-dbee67ebc7a7', true);

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                    , asset_id)
values
    (${investigationNotificationSentId6}, ${assetAsBuiltId20});

---
-- join investigation to asset
insert into assets_as_built_investigations
    (investigation_id       , asset_id)
values
    (${investigationSentId6}, ${assetAsBuiltId20});

---
update assets_as_built
    set in_investigation = false
    where id in (${assetAsBuiltId20});


