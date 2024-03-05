-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state CLOSED in Severity Critical for asBuilt asset Philips P-RightHeadBulb which is sent from BPNL000000000001 to BPNL000SUPPLIER2

---
insert into investigation
    (id                     , bpn      , close_reason      , created                              , description                                                 , status  , side    , accept_reason, decline_reason, updated                              , error_message)
values
    (${investigationSentId7}, ${bpnOwn}, 'Problem is fixed', current_timestamp - interval '6 days', 'Investigation on Philips RightHeadBulb due to broken fuse.', 'CLOSED', 'SENDER', null         , null          , current_timestamp - interval '1 hour', null);

---
-- reset sequence to highest next-val
select setval('investigation_id_seq1', (select max(i.id) from investigation i), true);

---
insert into investigation_notification
    (id                                  , contract_agreement_id, edc_url                    , notification_reference_id, created_by, send_to        , investigation_id       , target_date                          , severity  , created_by_name, send_to_name       , edc_notification_id                   , status    , created                              , updated                              , message_id                            , is_initial)
values
    (${investigationNotificationSentId7a}, 'contractAgreementId', 'http://localhost:5001/edc', null                     , ${bpnOwn} , ${bpnSupplier2}, ${investigationSentId7}, current_timestamp + interval '1 week', 'CRITICAL', ${bpnOwnName}  , ${bpnSupplier2Name}, '3ac2239a-e63f-4c19-b3b3-e6a2e5a240da', 'CANCELED', current_timestamp - interval '6 days', current_timestamp - interval '1 hour', '749b31e9-9e73-4699-9470-dbee67ebc7a7', true);

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                     , asset_id)
values
    (${investigationNotificationSentId7a}, ${assetAsBuiltId06});

---
-- join investigation to asset
insert into assets_as_built_investigations
    (investigation_id       , asset_id)
values
    (${investigationSentId7}, ${assetAsBuiltId06});

---
-- CLOSED by sender notification message
insert into investigation_notification
    (id                                  , contract_agreement_id, edc_url                    , notification_reference_id, created_by, send_to        , investigation_id       , target_date                          , severity  , created_by_name, send_to_name       , edc_notification_id                   , status    , created                              , updated                              , message_id                            , is_initial)
values
    (${investigationNotificationSentId7b}, 'contractAgreementId', 'http://localhost:5001/edc', null                     , ${bpnOwn} , ${bpnSupplier2}, ${investigationSentId7}, current_timestamp + interval '1 week', 'CRITICAL', ${bpnOwnName}  , ${bpnSupplier2Name}, '8925f21f-09eb-4789-81fb-ec221e9e1561', 'CANCELED', current_timestamp - interval '6 days', current_timestamp - interval '1 hour', '207ba6cf-217b-401d-a5da-69cac8b154a5', false);

---
-- join CLOSED notification to asset
insert into assets_as_built_notifications
    (notification_id                     , asset_id)
values
    (${investigationNotificationSentId7b}, ${assetAsBuiltId06});
