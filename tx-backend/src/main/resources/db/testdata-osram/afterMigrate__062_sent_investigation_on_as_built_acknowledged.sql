-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state ACKNOWLEDGED in Severity Life-threatening for asBuilt asset Schott Glass bulb which is sent from BPNL00000000GNDU to BPNL00SUPPLIER21

---
insert into investigation
    (id                     , bpn      , close_reason, created                              , description                          , status        , side    , accept_reason, decline_reason, updated                              , error_message)
values
    (${investigationSentId2}, ${bpnOwn}, null        , current_timestamp - interval '3 days', 'Investigation on Schott Glass bulb.', 'ACKNOWLEDGED', 'SENDER', null         , null          , current_timestamp - interval '1 hour', null);

---
-- reset sequence to highest next-val
select setval('investigation_id_seq', (select max(i.id) from investigation i), true);

-- TODO HGO@2023-12-07_17:48 is status 3 correct here?
---
insert into investigation_notification
    (id                                  , contract_agreement_id, edc_url                                                 , notification_reference_id, created_by, send_to        , investigation_id       , target_date                          , severity, created_by_name, send_to_name, edc_notification_id                   , status, created                              , updated                              , message_id                            , is_initial)
values
    (${investigationNotificationSentId2a}, 'contractAgreementId', 'http://localhost:8082/api/qualitynotifications/receive', null                     , ${bpnOwn} , ${bpnSupplier21}, ${investigationSentId2}, current_timestamp + interval '4 days', 3       , 'Osram'        , 'Schott'    , '3ac2239a-e63f-4c19-b3b3-e6a2e5a240da', 3     , current_timestamp - interval '3 days', current_timestamp - interval '1 hour', '749b31e9-9e73-4699-9470-dbee67ebc7a7', true);

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                , asset_id)
values
    (${investigationNotificationSentId2a}, ${assetAsBuiltId09});

---
-- join investigation to asset
insert into assets_as_built_investigations
    (investigation_id   , asset_id)
values
    (${investigationSentId2}, ${assetAsBuiltId09});

---
update assets_as_built
    set in_investigation = true
    where id in (${assetAsBuiltId09});

---
-- ACK by receiver notification message
insert into investigation_notification
    (id                                  , contract_agreement_id, edc_url                                                 , notification_reference_id, created_by     , send_to  , investigation_id       , target_date                          , severity, created_by_name, send_to_name, edc_notification_id                   , status , created                              , updated                              , message_id                            , is_initial)
values
    (${investigationNotificationSentId2b}, 'contractAgreementId', 'http://localhost:8082/api/qualitynotifications/receive', null                     , ${bpnSupplier21}, ${bpnOwn}, ${investigationSentId2}, current_timestamp + interval '4 days', 3       , 'Schott'       , 'Osram'     , '8925f21f-09eb-4789-81fb-ec221e9e1561', 3      , current_timestamp - interval '3 days', current_timestamp - interval '1 hour', '207ba6cf-217b-401d-a5da-69cac8b154a5', false);

---
-- join ACK notification to asset
insert into assets_as_built_notifications
    (notification_id                     , asset_id)
values
    (${investigationNotificationSentId2b}, ${assetAsBuiltId09});
