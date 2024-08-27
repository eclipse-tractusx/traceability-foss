-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state ACCEPTED in Severity Minor for asBuilt asset BrightSolutions Left Xenon Gland which is sent from BPNL000SUPPLIER2 to BPNL000000000001

---
insert into notification
    (id                              , title                                 , bpn                  , initial_receiver_bpn       , created                               , description             , status         , side               , target_date                           , severity                              , updated   , type)
values
    (${alertReceivedId3}             , ''                                    , ${bpnSupplier2}      , null                       , current_timestamp - interval '4 days' , 'Alert about Left Gland', 'ACCEPTED'     , 'RECEIVER'         , current_timestamp + interval '1 month', 'MINOR'                               , null      , 'ALERT');

---
-- reset sequence to highest next-val
select setval('notification_id_seq'  , (select max(n.id) from notification n), true);

---
-- initial message
insert into notification_message
    (id                              , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by              , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status    , created                                , updated                                , message_id                            , is_initial, error_message)
values
    (${alertNotificationReceivedId3a}, ${alertReceivedId3}                   , null                 , null                       , 'be320b81-01ab-46fd-baf5-4e182978c31e', ${bpnSupplier2}         , ${bpnOwn}      , ${bpnSupplier2Name}, ${bpnOwnName}                         , 'c7968bc9-22d0-48d8-8269-10a9043fcf47', 'RECEIVED', current_timestamp - interval '3 days'  , current_timestamp                      , '68ebeb5f-158d-480e-b466-24304842c22c', false     , null);

---
-- join initial notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id         , asset_id)
values
    (${alertNotificationReceivedId3a}, ${assetAsBuiltId05});

---
-- join alert to asset
insert into assets_as_built_notifications
    (notification_id                 , asset_id)
values
    (${alertReceivedId3}             , ${assetAsBuiltId05});

---
---
-- ACCEPTED by receiver notification message
insert into notification_message
    (id                              , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by              , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status    , created                                , updated                                , message_id                            , is_initial, error_message)
values
    (${alertNotificationReceivedId3b}, ${alertReceivedId3}                   , 'contractAgreementId', 'http://localhost:5001/edc', '6df59246-9127-430c-9135-bbb6741fe734', ${bpnOwn}               , ${bpnSupplier2}, ${bpnOwnName}      , ${bpnSupplier2Name}                   , 'a813eba6-9f11-4079-95ba-46aaa8257761', 'ACCEPTED', current_timestamp - interval '12 hours', current_timestamp - interval '12 hours', null                                  , false     , null);

---
-- join ACCEPTED notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id         , asset_id)
values
    (${alertNotificationReceivedId3b}, ${assetAsBuiltId05});
