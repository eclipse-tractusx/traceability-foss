-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state DECLINED in Severity Life-threatening for asBuilt asset Control Unit big which is sent from BPNL000CUSTOMER2 to BPNL000000000001

---
insert into notification
    (id                                      , title                                 , bpn                  , initial_receiver_bpn       , created                               , description                                            , status         , side               , target_date                          , severity                              , updated                               , type)
values
    (${investigationReceivedId4}             , ''                                    , ${bpnCustomer2}      , null                       , current_timestamp - interval '4 days' , 'Investigation on Control Unit big due to malfunction.', 'DECLINED'     , 'RECEIVER'         , current_timestamp + interval '3 days', 'LIFE_THREATENING'                    , current_timestamp - interval '1 hours', 'INVESTIGATION');

---
-- reset sequence to highest next-val
select setval('notification_id_seq'          , (select max(n.id) from notification n), true);

---
insert into notification_message
    (id                                      , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by                                             , send_to        , created_by_name    , send_to_name                         , edc_notification_id                   , status                                , created                              , updated                               , message_id                            , is_initial, error_message)
values
    (${investigationNotificationReceivedId4a}, ${investigationReceivedId4}           , null                 , null                       , '8925f21f-09eb-4789-81fb-ec221e9e1561', ${bpnCustomer2}                                        , ${bpnOwn}      , ${bpnCustomer2Name}, ${bpnOwnName}                        , '8925f21f-09eb-4789-81fb-ec221e9e1561', 'DECLINED'                            , current_timestamp - interval '4 days', current_timestamp - interval '2 hours', 'e04f75e8-d37b-42e4-8cf7-6127f35f3ed5', false     , null);

---
-- join notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id                 , asset_id)
values
    (${investigationNotificationReceivedId4a}, ${assetAsBuiltId16});

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                         , asset_id)
values
    (${investigationReceivedId4}             , ${assetAsBuiltId16});

---
---
-- DECLINED by receiver notification message
insert into notification_message
    (id                                      , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by                                             , send_to        , created_by_name    , send_to_name                         , edc_notification_id                   , status                                , created                              , updated                               , message_id                            , is_initial, error_message)
values
    (${investigationNotificationReceivedId4b}, ${investigationReceivedId4}           , 'contractAgreementId', 'http://localhost:5001/edc', null                                  , ${bpnOwn}                                              , ${bpnCustomer2}, ${bpnOwnName}      , ${bpnCustomer2Name}                  , '8925f21f-09eb-4789-81fb-ec221e9e1561', 'DECLINED'                            , current_timestamp - interval '4 days', current_timestamp - interval '1 hour' , '207ba6cf-217b-401d-a5da-69cac8b154a5', false     , null);

---
-- join DECLINED notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id                 , asset_id)
values
    (${investigationNotificationReceivedId4b}, ${assetAsBuiltId16});
