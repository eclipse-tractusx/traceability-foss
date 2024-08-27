-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state DECLINED in Severity Major for asBuilt asset CableCraft Electric Fuse small which is sent from BPNL000SUPPLIER3 to BPNL000000000001

---
insert into notification
    (id                              , title                                 , bpn                  , initial_receiver_bpn       , created                               , description                      , status         , side               , target_date                           , severity                              , updated   , type)
values
    (${alertReceivedId4}             , ''                                    , ${bpnSupplier3}      , null                       , current_timestamp - interval '5 days' , 'Alert about Electric Fuse Small', 'DECLINED'     , 'RECEIVER'         , current_timestamp + interval '1 month', 'MAJOR'                               , null      , 'ALERT');

---
-- reset sequence to highest next-val
select setval('notification_id_seq'  , (select max(n.id) from notification n), true);

---
-- initial message
insert into notification_message
    (id                              , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by                       , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status    , created                                , updated                                , message_id                            , is_initial, error_message)
values
    (${alertNotificationReceivedId4a}, ${alertReceivedId4}                   , null                 , null                       , '93ea7f66-8279-42cf-ad41-5e0a228412e6', ${bpnSupplier3}                  , ${bpnOwn}      , ${bpnSupplier3Name}, ${bpnOwnName}                         , 'de87af61-5eda-4f3f-a069-70d9ee60a05e', 'RECEIVED', current_timestamp - interval '4 days'  , current_timestamp                      , '68ebeb5f-158d-480e-b466-24304842c22c', false     , null);

---
-- join initial notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id         , asset_id)
values
    (${alertNotificationReceivedId4a}, ${assetAsBuiltId07});

---
-- join alert to asset
insert into assets_as_built_notifications
    (notification_id                 , asset_id)
values
    (${alertReceivedId4}             , ${assetAsBuiltId07});

---
---
-- DECLINED by receiver notification message
insert into notification_message
    (id                              , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by                       , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status    , created                                , updated                                , message_id                            , is_initial, error_message)
values
    (${alertNotificationReceivedId4b}, ${alertReceivedId4}                   , 'contractAgreementId', 'http://localhost:5001/edc', '1ceb2acc-0079-4ed1-bd7e-ea0aed0ae037', ${bpnOwn}                        , ${bpnSupplier3}, ${bpnOwnName}      , ${bpnSupplier3Name}                   , 'f58c8357-6da6-45ee-b144-4da896224939', 'ACCEPTED', current_timestamp - interval '12 hours', current_timestamp - interval '12 hours', null                                  , false     , null);

---
-- join DECLINED notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id         , asset_id)
values
    (${alertNotificationReceivedId4b}, ${assetAsBuiltId07});
