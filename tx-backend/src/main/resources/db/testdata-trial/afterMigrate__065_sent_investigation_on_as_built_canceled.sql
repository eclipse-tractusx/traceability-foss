-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state CANCELED in Severity Life-threatening for asBuilt asset LuxLite LED Orange Turn which is sent from BPNL000000000001 to BPNL000SUPPLIER5

---
insert into notification
    (id                                 , title                                 , bpn                  , initial_receiver_bpn       , created                              , description                                             , status         , side           , target_date                          , severity                              , updated                              , type)
values
    (${investigationSentId6}            , ''                                    , ${bpnOwn}            , null                       , current_timestamp - interval '7 days', 'Investigation on LED Orange Turn due to strange color.', 'CANCELED'     , 'SENDER'       , current_timestamp + interval '2 days', 'LIFE_THREATENING'                    , current_timestamp - interval '1 hour', 'INVESTIGATION');

---
-- reset sequence to highest next-val
select setval('notification_id_seq'     , (select max(n.id) from notification n), true);

---
insert into notification_message
    (id                                 , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id            , created_by                                              , send_to        , created_by_name, send_to_name                         , edc_notification_id                   , status                               , created                              , updated                              , message_id                            , is_initial, error_message)
values
    (${investigationNotificationSentId6}, ${investigationSentId6}               , 'contractAgreementId', 'http://localhost:5001/edc', null                                 , ${bpnOwn}                                               , ${bpnSupplier5}, ${bpnOwnName}  , ${bpnSupplier5Name}                  , '3ac2239a-e63f-4c19-b3b3-e6a2e5a240da', 'CLOSED'                             , current_timestamp - interval '7 days', current_timestamp - interval '1 hour', '749b31e9-9e73-4699-9470-dbee67ebc7a7', true      , null);

---
-- join notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id            , asset_id)
values
    (${investigationNotificationSentId6}, ${assetAsBuiltId12});

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                    , asset_id)
values
    (${investigationSentId6}            , ${assetAsBuiltId12});
