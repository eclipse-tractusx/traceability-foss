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
select setval('investigation_id_seq1', (select max(i.id) from investigation i), true);

---
insert into investigation_notification
    (id                                  , contract_agreement_id, edc_url, notification_reference_id, created_by, send_to         , investigation_id       , target_date                          , severity          , created_by_name, send_to_name        , edc_notification_id                   , status        , created                              , updated                              , message_id, is_initial)
values
    (${investigationNotificationSentId2a}, 'contractAgreementId', null   , null                     , ${bpnOwn} , ${bpnSupplier21}, ${investigationSentId2}, current_timestamp + interval '4 days', 'LIFE_THREATENING', ${bpnOwnName}  , ${bpnSupplier21Name}, '92559ce9-d71e-46b3-989f-791c9970877c', 'ACKNOWLEDGED', current_timestamp - interval '3 days', current_timestamp - interval '1 hour', null      , true);

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                     , asset_id)
values
    (${investigationNotificationSentId2a}, ${assetAsBuiltId09});

---
-- join investigation to asset
insert into assets_as_built_investigations
    (investigation_id       , asset_id)
values
    (${investigationSentId2}, ${assetAsBuiltId09});

---
-- ACK by receiver notification message
insert into investigation_notification
    (id                                  , contract_agreement_id, edc_url, notification_reference_id             , created_by      , send_to  , investigation_id       , target_date                          , severity          , created_by_name     , send_to_name , edc_notification_id                   , status        , created                              , updated, message_id, is_initial)
values
    (${investigationNotificationSentId2b}, 'contractAgreementId', null   , '92559ce9-d71e-46b3-989f-791c9970877c', ${bpnSupplier21}, ${bpnOwn}, ${investigationSentId2}, current_timestamp + interval '4 days', 'LIFE_THREATENING', ${bpnSupplier21Name}, ${bpnOwnName}, '92559ce9-d71e-46b3-989f-791c9970877c', 'ACKNOWLEDGED', current_timestamp - interval '2 days', null   , null      , false);

---
-- join ACK notification to asset
insert into assets_as_built_notifications
    (notification_id                     , asset_id)
values
    (${investigationNotificationSentId2b}, ${assetAsBuiltId09});
