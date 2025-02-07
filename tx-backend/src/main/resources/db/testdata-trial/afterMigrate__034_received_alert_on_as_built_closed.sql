-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https:                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state CLOSED in Severity Critical for asBuilt asset CableCraft Electric Fuse big which is sent from BPNL000SUPPLIER2 to BPNL000000000001

---
insert into notification
    (id                              , title                                 , bpn                  , initial_receiver_bpn       , created                               , description                    , status   , side               , target_date                           , severity                              , updated , type)
values
    (${alertReceivedId5}             , ''                                    , ${bpnSupplier2}      , 'Issue has been resolved'  , current_timestamp - interval '6 days' , 'Alert about Electric Fuse Big', 'CLOSED' , 'RECEIVER'         , current_timestamp + interval '1 month', 'CRITICAL'                            , null    , 'ALERT');

---
-- reset sequence to highest next-val
select setval('notification_id_seq'  , (select max(n.id) from notification n), true);

---
-- initial message
insert into notification_message
    (id                              , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by                     , send_to  , created_by_name    , send_to_name                          , edc_notification_id                   , status  , created                                , updated                                , message_id                            , is_initial, error_message)
values
    (${alertNotificationReceivedId5a}, ${alertReceivedId5}                   , null                 , null                       , '9c1290e1-7292-42fc-aef3-16f384e0bc76', ${bpnSupplier2}                , ${bpnOwn}, ${bpnSupplier2Name}, ${bpnOwnName}                         , '01d08ee0-ee35-48ca-90ae-e4eea05dba92', 'CLOSED', current_timestamp - interval '5 days'  , current_timestamp                      , '68ebeb5f-158d-480e-b466-24304842c22c', false     , null);

---
-- join initial notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id         , asset_id)
values
    (${alertNotificationReceivedId5a}, ${assetAsBuiltId08});

---
-- join alert to asset
insert into assets_as_built_notifications
    (notification_id                 , asset_id)
values
    (${alertReceivedId5}             , ${assetAsBuiltId08});

---
---
-- CLOSED by sender notification message
insert into notification_message
    (id                              , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by                     , send_to  , created_by_name    , send_to_name                          , edc_notification_id                   , status  , created                                , updated                                , message_id                            , is_initial, error_message)
values
    (${alertNotificationReceivedId5b}, ${alertReceivedId5}                   , 'contractAgreementId', 'http://localhost:5001/edc', 'cc5378bd-d769-41cc-b528-dcc0e37b230a', ${bpnSupplier2}                , ${bpnOwn}, ${bpnSupplier2Name}, ${bpnOwnName}                         , '4981b000-2da0-4e3c-9088-da62c8d475be', 'CLOSED', current_timestamp - interval '12 hours', current_timestamp - interval '12 hours', null                                  , false     , null);

---
-- join CLOSED notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id         , asset_id)
values
    (${alertNotificationReceivedId5b}, ${assetAsBuiltId08});
