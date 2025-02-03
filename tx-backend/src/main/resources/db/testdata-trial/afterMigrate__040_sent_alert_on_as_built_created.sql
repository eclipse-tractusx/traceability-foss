-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state CREATED in Severity Minor for asBuilt asset Xenon Left-Headlight which is sent from BPNL000000000001 to BPNL000CUSTOMER1

---
insert into notification
    (id                            , title                                 , bpn                  , initial_receiver_bpn, created                  , description                        , status         , side           , target_date                           , severity                   , updated  , type)
values
    (${alertSentId1}               , ''                                    , ${bpnOwn}            , null                , current_timestamp        , 'Second Alert about Left Headlight', 'CREATED'      , 'SENDER'       , current_timestamp + interval '1 month', 'MINOR'                    , null     , 'ALERT');

---
-- reset sequence to highest next-val
select setval('notification_id_seq', (select max(n.id) from notification n), true);

---
-- initial message
insert into notification_message
    (id                            , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id, created_by                         , send_to        , created_by_name, send_to_name                          , edc_notification_id        , status   , created                                 , updated          , message_id                            , is_initial, error_message)
values
    (${alertNotificationSentId1}   , ${alertSentId1}                       , null                 , null                , 'null'                   , ${bpnOwn}                          , ${bpnCustomer1}, ${bpnOwnName}  , ${bpnCustomer1Name}                   , ${alertNotificationSentId1}, 'CREATED', current_timestamp - interval '1 seconds', current_timestamp, '42e28782-bf4c-45a2-82b7-1757aa4b8772', true      , null          );

---
-- join initial notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id       , asset_id)
values
    (${alertNotificationSentId1}   , ${assetAsBuiltId01});

---
-- join alert to asset
insert into assets_as_built_notifications
    (notification_id               , asset_id)
values
    (${alertSentId1}               , ${assetAsBuiltId01});
