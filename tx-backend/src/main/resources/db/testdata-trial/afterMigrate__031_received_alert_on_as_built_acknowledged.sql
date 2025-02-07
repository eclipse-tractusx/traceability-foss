-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state ACKNOWLEDGED in Severity Life-threatening for asBuilt asset BrightSolutions Right Xenon Gland which is sent from BPNL000SUPPLIER2 to BPNL000000000001

---
insert into notification
    (id                              , title                                 , bpn                  , initial_receiver_bpn, created                               , description              , status         , side               , target_date                           , severity                              , updated       , type)
values
    (${alertReceivedId2}             , ''                                    , ${bpnSupplier2}      , null                , current_timestamp - interval '3 days' , 'Alert about Right Gland', 'ACKNOWLEDGED' , 'RECEIVER'         , current_timestamp + interval '1 month', 'LIFE_THREATENING'                    , null          , 'ALERT');

---
-- reset sequence to highest next-val
select setval('notification_id_seq'  , (select max(n.id) from notification n), true);

---
-- initial message
insert into notification_message
 (id                                 , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by               , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status        , created                                , updated                                , message_id, is_initial, error_message)
values
    (${alertNotificationReceivedId2a}, ${alertReceivedId2}                   , null                 , null                , '403818ab-28fc-428f-99b1-61077fede954', ${bpnSupplier2}          , ${bpnOwn}      , ${bpnSupplier2Name}, ${bpnOwnName}                         , '403818ab-28fc-428f-99b1-61077fede954', 'RECEIVED'    , current_timestamp - interval '2 days'  , current_timestamp                      , null      , false     , null);

---
-- join initial notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id         , asset_id)
values
    (${alertNotificationReceivedId2a}, ${assetAsBuiltId06});

---
-- join alert to asset
insert into assets_as_built_notifications
    (notification_id                 , asset_id)
values
    (${alertReceivedId2}             , ${assetAsBuiltId06});

---
---
-- ACK by receiver notification message
insert into notification_message
(id                                  , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by               , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status        , created                                , updated                                , message_id, is_initial, error_message)
values
    (${alertNotificationReceivedId2b}, ${alertReceivedId2}                   , null                 , null                , '403818ab-28fc-428f-99b1-61077fede954', ${bpnOwn}                , ${bpnSupplier2}, ${bpnOwnName}      , ${bpnSupplier2Name}                   , '403818ab-28fc-428f-99b1-61077fede954', 'ACKNOWLEDGED', current_timestamp - interval '12 hours', current_timestamp - interval '12 hours', null      , false     , null);

---
-- join ACK notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id         , asset_id)
values
    (${alertNotificationReceivedId2b}, ${assetAsBuiltId06});
