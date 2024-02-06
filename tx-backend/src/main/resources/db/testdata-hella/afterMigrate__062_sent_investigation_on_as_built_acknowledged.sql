-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state ACKNOWLEDGED in Severity Life-threatening for asBuilt asset Philips P-LeftHeadBulb which is sent from BPNL000000000001 to BPNL000SUPPLIER2

---
insert into investigation
    (id                     , bpn      , close_reason, created                              , description                                                         , status        , side    , accept_reason, decline_reason, updated                              , error_message)
values
    (${investigationSentId3}, ${bpnOwn}, null        , current_timestamp - interval '3 days', 'Investigation on Philips P-LeftHeadBulb due to occasional outages.', 'ACKNOWLEDGED', 'SENDER', null         , null          , current_timestamp - interval '1 hour', null);

---
-- reset sequence to highest next-val
select setval('investigation_id_seq1', (select max(i.id) from investigation i), true);

---
insert into investigation_notification
    (id                                  , contract_agreement_id, edc_url                                                 , notification_reference_id, created_by, send_to        , investigation_id       , target_date                          , severity          , created_by_name, send_to_name, edc_notification_id                   , status        , created                              , updated                              , message_id                            , is_initial)
values
    (${investigationNotificationSentId3a}, 'contractAgreementId', 'http://localhost:8082/api/qualitynotifications/receive', null                     , ${bpnOwn} , ${bpnSupplier2}, ${investigationSentId3}, current_timestamp + interval '4 days', 'LIFE_THREATENING', 'Hella'        , 'Philips'   , '3ac2239a-e63f-4c19-b3b3-e6a2e5a240da', 'ACKNOWLEDGED', current_timestamp - interval '3 days', current_timestamp - interval '1 hour', '749b31e9-9e73-4699-9470-dbee67ebc7a7', true);

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                     , asset_id)
values
    (${investigationNotificationSentId3a}, ${assetAsBuiltId05});

---
-- join investigation to asset
insert into assets_as_built_investigations
    (investigation_id       , asset_id)
values
    (${investigationSentId3}, ${assetAsBuiltId05});

---
-- update assets_as_built
--     set in_investigation = true
--     where id in (${assetAsBuiltId05});

---
-- ACK by receiver notification message
insert into investigation_notification
    (id                                  , contract_agreement_id, edc_url                                                 , notification_reference_id, created_by     , send_to  , investigation_id       , target_date                          , severity          , created_by_name, send_to_name, edc_notification_id                   , status        , created                              , updated                              , message_id                            , is_initial)
values
    (${investigationNotificationSentId3b}, 'contractAgreementId', 'http://localhost:8082/api/qualitynotifications/receive', null                     , ${bpnSupplier2}, ${bpnOwn}, ${investigationSentId3}, current_timestamp + interval '4 days', 'LIFE_THREATENING', 'Philips'      , 'Hella'     , '8925f21f-09eb-4789-81fb-ec221e9e1561', 'ACKNOWLEDGED', current_timestamp - interval '3 days', current_timestamp - interval '1 hour', '207ba6cf-217b-401d-a5da-69cac8b154a5', false);

---
-- join ACK notification to asset
insert into assets_as_built_notifications
    (notification_id                     , asset_id)
values
    (${investigationNotificationSentId3b}, ${assetAsBuiltId05});
