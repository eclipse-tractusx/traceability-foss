-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state CANCELED in Severity Minor for asBuilt asset Break Light which is sent from BPNL000000000001 to BPNL000CUSTOMER1

---
insert into notification
    (id                            , title                                 , bpn                  , initial_receiver_bpn       , created                              , description                        , status         , side           , target_date                           , severity                   , updated   , type)
values
    (${alertSentId6}               , ''                                    , ${bpnOwn}            , null                       , current_timestamp - interval '3 days', 'Cancelled Alert about Break Light', 'CANCELED'     , 'SENDER'       , current_timestamp + interval '1 month', 'MINOR'                    , null      , 'ALERT'  );

---
-- reset sequence to highest next-val
select setval('notification_id_seq', (select max(n.id) from notification n), true);

---
-- initial message
insert into notification_message
    (id                            , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id            , created_by                         , send_to        , created_by_name, send_to_name                          , edc_notification_id        , status    , created                              , updated          , message_id                            , is_initial, error_message)
values
    (${alertNotificationSentId6}   , ${alertSentId6}                       , 'contractAgreementId', 'http://localhost:5001/edc', 'null'                               , ${bpnOwn}                          , ${bpnCustomer1}, ${bpnOwnName}  , ${bpnCustomer1Name}                   , ${alertNotificationSentId6}, 'CANCELED', current_timestamp - interval '3 days', current_timestamp, 'bd6ca75b-9d1c-44bd-bc80-b3afca317daf', true      , null);

---
-- join initial notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id       , asset_id)
values
    (${alertNotificationSentId6}   , ${assetAsBuiltId18});

---
-- join alert to asset
insert into assets_as_built_notifications
    (notification_id               , asset_id)
values
    (${alertSentId6}               , ${assetAsBuiltId18});
