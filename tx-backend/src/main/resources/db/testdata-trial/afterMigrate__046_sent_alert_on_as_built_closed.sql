-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state CLOSED in Severity Critical for asBuilt asset Xenon Right-Headlight which is sent from BPNL000000000001 to BPNL000CUSTOMER1

---
insert into notification
    (id                            , title                                 , bpn                  , initial_receiver_bpn       , created                               , description                         , status         , side           , target_date                           , severity                              , updated , type)
values
    (${alertSentId7}               , ''                                    , ${bpnOwn}            , null   , current_timestamp - interval '2 days' , 'Closed Alert about Right Headlight', 'CLOSED'       , 'SENDER'       , current_timestamp + interval '1 month', 'CRITICAL'                            , null    , 'ALERT'  );

---
-- reset sequence to highest next-val
select setval('notification_id_seq', (select max(n.id) from notification n), true);

---
-- initial message
insert into notification_message
    (id                            , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by                          , send_to        , created_by_name, send_to_name                          , edc_notification_id                   , status  , created                              , updated                                , message_id                            , is_initial, error_message)
values
    (${alertNotificationSentId7a}  , ${alertSentId7}                       , 'contractAgreementId', 'http://localhost:5001/edc', 'null'                                , ${bpnOwn}                           , ${bpnCustomer1}, ${bpnOwnName}  , ${bpnCustomer1Name}                   , ${alertNotificationSentId7a}          , 'CLOSED', current_timestamp - interval '2 days', current_timestamp                      , '3ca2e271-bffb-4e34-83c5-13fc1c462062', true      , null);

---
-- join initial notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id       , asset_id)
values
    (${alertNotificationSentId7a}  , ${assetAsBuiltId02});

---
-- join alert to asset
insert into assets_as_built_notifications
    (notification_id               , asset_id)
values
    (${alertSentId7}               , ${assetAsBuiltId02});

---
-- CLOSED by sender notification message
insert into notification_message
    (id                            , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by                          , send_to        , created_by_name, send_to_name                          , edc_notification_id                   , status  , created                              , updated                                , message_id                            , is_initial, error_message)
values
    (${alertNotificationSentId7b}  , ${alertSentId7}                       , 'contractAgreementId', 'http://localhost:5001/edc', '04d9d9b0-26cc-4209-a039-825f43cca44e', ${bpnOwn}                           , ${bpnCustomer1}, ${bpnOwnName}  , ${bpnCustomer1Name}                   , '6eb2499c-6ad0-426d-96d9-a723fab7da7b', 'CLOSED', current_timestamp - interval '2 days', current_timestamp - interval '12 hours', null                                  , false     , null);

---
-- join CLOSED notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id       , asset_id)
values
    (${alertNotificationSentId7b}  , ${assetAsBuiltId02});
