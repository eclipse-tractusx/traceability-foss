-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state CREATED in Severity Critical for asBuilt asset LuminaTech Left Headbulb which is sent from BPNL000000000001 to BPNL000SUPPLIER1

---
insert into notification
    (id                                 , title                                 , bpn                  , initial_receiver_bpn, created                             , description                                                  , status         , side           , target_date                          , severity                              , updated  , type)
values
    (${investigationSentId1}            , ''                                    , ${bpnOwn}            , null                , current_timestamp - interval '1 day', 'Investigation on Left Headbulb due to excessive brightness.', 'CREATED'      , 'SENDER'       , current_timestamp + interval '1 week', 'CRITICAL'                            , null     , 'INVESTIGATION');

---
-- reset sequence to highest next-val
select setval('notification_id_seq'     , (select max(n.id) from notification n), true);

---
insert into notification_message
    (id                                 , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id           , created_by                                                   , send_to        , created_by_name, send_to_name                         , edc_notification_id                   , status   , created                             , updated                              , message_id                            , is_initial, error_message)
values
    (${investigationNotificationSentId1}, ${investigationSentId1}               , null                 , null                , null                                , ${bpnOwn}                                                    , ${bpnSupplier1}, ${bpnOwnName}  , ${bpnSupplier1Name}                  , 'c01353db-640a-44c4-9a87-28fa3a950a95', 'CREATED', current_timestamp - interval '1 day', current_timestamp - interval '1 hour', '71d7cb88-a208-434b-993e-74aeb331fd11', true      , null          );

---
-- join notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id            , asset_id)
values
    (${investigationNotificationSentId1}, ${assetAsBuiltId03});

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                    , asset_id)
values
    (${investigationSentId1}            , ${assetAsBuiltId03});
