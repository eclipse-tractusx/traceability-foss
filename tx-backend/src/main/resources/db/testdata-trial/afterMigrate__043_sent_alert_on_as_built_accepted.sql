-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state ACCEPTED in Severity Minor for asBuilt asset Control Unit big which is sent from BPNL000000000001 to BPNL000CUSTOMER2

---
insert into notification
    (id                            , title                                 , bpn                  , initial_receiver_bpn, created                               , description                   , status         , side               , target_date                           , severity                              , updated   , type)
values
    (${alertSentId4}               , ''                                    , ${bpnOwn}            , null                , current_timestamp - interval '5 days' , 'Alert about Control Unit big', 'ACCEPTED'     , 'SENDER'           , current_timestamp + interval '1 month', 'MINOR'                               , null      , 'ALERT');

---
-- reset sequence to highest next-val
select setval('notification_id_seq', (select max(n.id) from notification n), true);

---
-- initial message
insert into notification_message
    (id                            , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by                    , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status    , created                              , updated          , message_id, is_initial, error_message)
values
    (${alertNotificationSentId4a}  , ${alertSentId4}                       , 'contractAgreementId', null                , '8e777e64-f7f3-44e2-b78e-75da964f1531', ${bpnOwn}                     , ${bpnCustomer2}, ${bpnOwnName}      , ${bpnCustomer2Name}                   , '8e777e64-f7f3-44e2-b78e-75da964f1531', 'CREATED' , current_timestamp - interval '5 days', current_timestamp, null      , true      , null);

---
-- join initial notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id       , asset_id)
values
    (${alertNotificationSentId4a}  , ${assetAsBuiltId16});

---
-- join alert to asset
insert into assets_as_built_notifications
    (notification_id               , asset_id)
values
    (${alertSentId4}               , ${assetAsBuiltId16});

---
-- ACCEPTED by receiver notification message
insert into notification_message
    (id                            , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by                    , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status    , created                              , updated          , message_id, is_initial, error_message)
values
    (${alertNotificationSentId4b}  , ${alertSentId4}                       , 'contractAgreementId', null                , '8e777e64-f7f3-44e2-b78e-75da964f1531', ${bpnCustomer2}               , ${bpnOwn}      , ${bpnCustomer2Name}, ${bpnOwnName}                         , '8e777e64-f7f3-44e2-b78e-75da964f1531', 'ACCEPTED', current_timestamp - interval '3 days', current_timestamp, null      , false     , null);

---
-- join ACCEPTED notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id       , asset_id)
values
    (${alertNotificationSentId4b}  , ${assetAsBuiltId16});
