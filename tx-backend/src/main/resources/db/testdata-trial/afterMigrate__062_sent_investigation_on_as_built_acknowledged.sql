-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state ACKNOWLEDGED in Severity Life-threatening for asBuilt asset BrightSolutions Left Xenon Gland which is sent from BPNL000000000001 to BPNL000SUPPLIER2

---
insert into notification
    (id                                  , title                                 , bpn                  , initial_receiver_bpn, created                               , description                                                   , status         , side               , target_date                          , severity                              , updated                              , type)
values
    (${investigationSentId3}             , ''                                    , ${bpnOwn}            , null                , current_timestamp - interval '3 days' , 'Investigation on Left Xenon Gland due to occasional outages.', 'ACKNOWLEDGED' , 'SENDER'           , current_timestamp + interval '4 days', 'LIFE_THREATENING'                    , current_timestamp - interval '1 hour', 'INVESTIGATION');

---
-- reset sequence to highest next-val
select setval('notification_id_seq'      , (select max(n.id) from notification n), true);

---
insert into notification_message
    (id                                  , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by                                                    , send_to        , created_by_name    , send_to_name                         , edc_notification_id                   , status                               , created                              , updated                              , message_id, is_initial, error_message)
values
    (${investigationNotificationSentId3a}, ${investigationSentId3}               , 'contractAgreementId', null                , null                                  , ${bpnOwn}                                                     , ${bpnSupplier2}, ${bpnOwnName}      , ${bpnSupplier2Name}                  , '5679b6fe-7463-4439-9e87-7f8622d57c22', 'ACKNOWLEDGED'                       , current_timestamp - interval '3 days', current_timestamp - interval '1 hour', null      , true      , null);

---
-- join notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id             , asset_id)
values
    (${investigationNotificationSentId3a}, ${assetAsBuiltId05});

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                     , asset_id)
values
    (${investigationSentId3}             , ${assetAsBuiltId05});

---
-- ACK by receiver notification message
insert into notification_message
    (id                                  , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by                                                    , send_to        , created_by_name    , send_to_name                         , edc_notification_id                   , status                               , created                              , updated                              , message_id, is_initial, error_message)
values
    (${investigationNotificationSentId3b}, ${investigationSentId3}               , 'contractAgreementId', null                , '5679b6fe-7463-4439-9e87-7f8622d57c22', ${bpnSupplier2}                                               , ${bpnOwn}      , ${bpnSupplier2Name}, ${bpnOwnName}                        , '5679b6fe-7463-4439-9e87-7f8622d57c22', 'ACKNOWLEDGED'                       , current_timestamp - interval '2 days', null                                 , null      , false     , null);

---
-- join ACK notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id             , asset_id)
values
    (${investigationNotificationSentId3b}, ${assetAsBuiltId05});
