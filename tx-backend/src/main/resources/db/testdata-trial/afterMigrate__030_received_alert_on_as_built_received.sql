-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state RECEIVED in Severity Critical for asBuilt asset Lumintech Left Headbulb which is sent from BPNL000SUPPLIER1 to BPNL000000000001

---
insert into notification
    (id                             , title                                 , bpn                  , initial_receiver_bpn, created                               , description                , status    , side               , target_date                           , severity                              , updated   , type)
values
    (${alertReceivedId1}            , ''                                    , ${bpnSupplier1}      , null                , current_timestamp - interval '2 days' , 'Alert about Left Headbulb', 'RECEIVED', 'RECEIVER'         , current_timestamp + interval '1 month', 'CRITICAL'                            , null      , 'ALERT'  );

---
-- reset sequence to highest next-val
select setval('notification_id_seq' , (select max(n.id) from notification n), true);

---
insert into notification_message
 (id                                , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by                 , send_to   , created_by_name    , send_to_name                          , edc_notification_id                   , status    , created                             , updated, message_id                            , is_initial, error_message)
values
    (${alertNotificationReceivedId1}, ${alertReceivedId1}                   , null                 , null                , 'ded38f08-1b9c-497d-b994-6feba92b7f41', ${bpnSupplier1}            , ${bpnOwn} , ${bpnSupplier1Name}, ${bpnOwnName}                         , 'ded38f08-1b9c-497d-b994-6feba92b7f41', 'RECEIVED', current_timestamp - interval '1 day', null   , '7d0891d2-4940-4802-b0bc-cc30f9e94e76', false     , null);

---
-- join notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id        , asset_id)
values
    (${alertNotificationReceivedId1}, ${assetAsBuiltId03});

---
-- join alert to asset
insert into assets_as_built_notifications
    (notification_id                , asset_id)
values
    (${alertReceivedId1}            , ${assetAsBuiltId03});
