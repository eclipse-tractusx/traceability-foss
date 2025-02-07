-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state CLOSED in Severity Minor for asBuilt asset Turn Light which is sent from BPNL000CUSTOMER1 to BPNL000000000001

---
insert into notification
    (id                                      , title                                 , bpn                  , initial_receiver_bpn       , created                               , description                                      , status   , side               , target_date                           , severity                              , updated                               , type)
values
    (${investigationReceivedId5}             , ''                                    , ${bpnCustomer2}      , null                       , current_timestamp - interval '5 days' , 'Investigation on Turn Light due to malfunction.', 'CLOSED' , 'RECEIVER'         , current_timestamp + interval '1 month', 'MINOR'                               , current_timestamp - interval '1 hours', 'INVESTIGATION');

---
-- reset sequence to highest next-val
select setval('notification_id_seq'          , (select max(n.id) from notification n), true);

---
insert into notification_message
    (id                                      , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by                                       , send_to  , created_by_name    , send_to_name                          , edc_notification_id                   , status                                , created                               , updated                               , message_id                            , is_initial, error_message)
values
    (${investigationNotificationReceivedId5a}, ${investigationReceivedId5}           , null                 , null                       , '8925f21f-09eb-4789-81fb-ec221e9e1561', ${bpnCustomer1}                                  , ${bpnOwn}, ${bpnCustomer1Name}, ${bpnOwnName}                         , '8925f21f-09eb-4789-81fb-ec221e9e1561', 'CLOSED'                              , current_timestamp - interval '5 days' , current_timestamp - interval '2 hours', 'e04f75e8-d37b-42e4-8cf7-6127f35f3ed5', false     , null);

---
-- join notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id                 , asset_id)
values
    (${investigationNotificationReceivedId5a}, ${assetAsBuiltId17});

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                         , asset_id)
values
    (${investigationReceivedId5}             , ${assetAsBuiltId17});

---
---
-- CLOSED by sender notification message
insert into notification_message
    (id                                      , notification_id                       , contract_agreement_id, edc_url                    , notification_reference_id             , created_by                                       , send_to  , created_by_name    , send_to_name                          , edc_notification_id                   , status                                , created                               , updated                               , message_id                            , is_initial, error_message)
values
    (${investigationNotificationReceivedId5b}, ${investigationReceivedId5}           , 'contractAgreementId', 'http://localhost:5001/edc', null                                  , ${bpnCustomer1}                                  , ${bpnOwn}, ${bpnCustomer1Name}, ${bpnOwnName}                         , '8925f21f-09eb-4789-81fb-ec221e9e1561', 'CLOSED'                              , current_timestamp - interval '5 days ', current_timestamp - interval '1 hour' , '207ba6cf-217b-401d-a5da-69cac8b154a5', false     , null);

---
-- join CLOSED notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id                 , asset_id)
values
    (${investigationNotificationReceivedId5b}, ${assetAsBuiltId17});
