-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state ACKNOWLEDGED in Severity Minor for asBuilt asset Xenon Right-Headlight which is sent from BPNL000CUSTOMER1 to BPNL000000000001

---
insert into notification
    (id                                      , title                                 , bpn                  , initial_receiver_bpn, created                               , description                                           , status         , side               , target_date                           , severity                              , updated                               , type)
values
    (${investigationReceivedId2}             , ''                                    , ${bpnCustomer1}      , null                , current_timestamp - interval '2 days' , 'Investigation on Right Headlight due to malfunction.', 'ACKNOWLEDGED' , 'RECEIVER'         , current_timestamp + interval '1 month', 'MINOR'                               , current_timestamp - interval '1 hours', 'INVESTIGATION');

---
-- reset sequence to highest next-val
select setval('notification_id_seq'          , (select max(n.id) from notification n), true);

---
insert into notification_message
    (id                                      , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by                                            , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status                                , created                                                  , updated                               , message_id, is_initial, error_message)
values
    (${investigationNotificationReceivedId2a}, ${investigationReceivedId2}           , null                 , null                , 'c422e2a7-d037-499c-9ec2-44e3bc4f5815', ${bpnCustomer1}                                       , ${bpnOwn}      , ${bpnCustomer1Name}, ${bpnOwnName}                         , 'c422e2a7-d037-499c-9ec2-44e3bc4f5815', 'RECEIVED'                            , current_timestamp - interval '2 days'                    , current_timestamp - interval '2 hours', null      , false     , null);


---
-- join notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id                 , asset_id)
values
    (${investigationNotificationReceivedId2a}, ${assetAsBuiltId02});

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                         , asset_id)
values
    (${investigationReceivedId2}             , ${assetAsBuiltId02});

---
---
-- ACK by receiver notification message
insert into notification_message
    (id                                      , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by                                            , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status                                , created                                                  , updated                               , message_id, is_initial, error_message)
values
    (${investigationNotificationReceivedId2b}, ${investigationReceivedId2}           , 'contractAgreementId', null                , null                                  , ${bpnOwn}                                             , ${bpnCustomer1}, ${bpnOwnName}      , ${bpnCustomer1Name}                   , 'c422e2a7-d037-499c-9ec2-44e3bc4f5815', 'ACKNOWLEDGED'                        , current_timestamp - interval '2 days' + interval '1 hour', current_timestamp - interval '1 hour' , null      , false     , null);

---
-- join ACK notification to asset
insert into assets_as_built_notification_messages
    (notification_message_id                 , asset_id)
values
    (${investigationNotificationReceivedId2b}, ${assetAsBuiltId02});

