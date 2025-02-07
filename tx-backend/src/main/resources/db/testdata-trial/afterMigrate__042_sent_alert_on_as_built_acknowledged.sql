-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state ACKNOWLEDGED in Severity Life-threatening for asBuilt asset Control Unit small which is sent from BPNL000000000001 to BPNL000CUSTOMER2

---
insert into notification
    (id                            , title                                 , bpn                  , initial_receiver_bpn, created                               , description                     , status         , side               , target_date        , severity                              , updated       , type)
values
    (${alertSentId3}               , ''                                    , ${bpnOwn}            , null                , current_timestamp - interval '1 day'  , 'Alert about Control Unit Small', 'ACKNOWLEDGED' , 'SENDER'           , null               , 'LIFE_THREATENING'                    , null          , 'ALERT');

---
-- reset sequence to highest next-val
select setval('notification_id_seq', (select max(n.id) from notification n), true);

---
-- initial message
insert into notification_message
    (id                            , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by                      , send_to        , created_by_name    , send_to_name       , edc_notification_id                   , status        , created                                , updated                                , message_id, is_initial, error_message)
values
    (${alertNotificationSentId3a}  , ${alertSentId3}                       , 'contractAgreementId', null                , '7354895f-9e79-4582-af35-7d5b820ac9b6', ${bpnOwn}                       , ${bpnCustomer2}, ${bpnOwnName}      , ${bpnCustomer2Name}, '7354895f-9e79-4582-af35-7d5b820ac9b6', 'CREATED'     , current_timestamp - interval '1 day'   , current_timestamp                      , null      , true      , null);

---
-- join initial notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id       , asset_id)
values
    (${alertNotificationSentId3a}  , ${assetAsBuiltId15});

---
-- join alert to asset
insert into assets_as_built_notifications
    (notification_id               , asset_id)
values
    (${alertSentId3}               , ${assetAsBuiltId15});

---
-- ACK by receiver notification message
insert into notification_message
    (id                            , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by                      , send_to        , created_by_name    , send_to_name       , edc_notification_id                   , status        , created                                , updated                                , message_id, is_initial, error_message)
 values
    (${alertNotificationSentId3b}  , ${alertSentId3}                       , 'contractAgreementId', null                , '7354895f-9e79-4582-af35-7d5b820ac9b6', ${bpnCustomer2}                 , ${bpnOwn}      , ${bpnCustomer2Name}, ${bpnOwnName}      , '7354895f-9e79-4582-af35-7d5b820ac9b6', 'ACKNOWLEDGED', current_timestamp - interval '12 hours', current_timestamp - interval '12 hours', null      , false     , null);

---
-- join ACK notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id       , asset_id)
values
    (${alertNotificationSentId3b}  , ${assetAsBuiltId15});
