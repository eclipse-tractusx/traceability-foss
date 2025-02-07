-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https: //documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an alert in state DECLINED in Severity Major for asBuilt asset Turn Light which is sent from BPNL000000000001 to BPNL000CUSTOMER1

---
insert into notification
    (id                            , title                                 , bpn                  , initial_receiver_bpn, created                               , description             , status         , side               , target_date                           , severity                              , updated   , type)
values
    (${alertSentId5}               , ''                                    , ${bpnOwn}            , null                , current_timestamp - interval '4 days' , 'Alert about Turn Light', 'DECLINED'     , 'SENDER'           , current_timestamp + interval '2 weeks', 'MAJOR'                               , null      , 'ALERT');

---
-- reset sequence to highest next-val
select setval('notification_id_seq', (select max(n.id) from notification n), true);

---
-- initial message
insert into notification_message
    (id                            , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by              , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status    , created                              , updated                                , message_id, is_initial, error_message)
values
    (${alertNotificationSentId5a}  , ${alertSentId5}                       , null                 , null                , 'null'                                , ${bpnOwn}               , ${bpnCustomer1}, ${bpnOwnName}      , ${bpnCustomer1Name}                   , 'fe40ca1c-0d4d-4af0-9ccc-415113f4b7a1', 'CREATED' , current_timestamp - interval '4 days', current_timestamp                      , null      , true      , null          );

---
-- join initial notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id       , asset_id)
values
    (${alertNotificationSentId5a}  , ${assetAsBuiltId17});

---
-- join alert to asset
insert into assets_as_built_notifications
    (notification_id               , asset_id)
values
    (${alertSentId5}               , ${assetAsBuiltId17});

---
-- DECLINED by receiver notification message
insert into notification_message
    (id                            , notification_id                       , contract_agreement_id, edc_url             , notification_reference_id             , created_by              , send_to        , created_by_name    , send_to_name                          , edc_notification_id                   , status    , created                              , updated                                , message_id, is_initial, error_message)
values
    (${alertNotificationSentId5b}  , ${alertSentId5}                       , null                 , null                , 'fe40ca1c-0d4d-4af0-9ccc-415113f4b7a1', ${bpnCustomer1}         , ${bpnOwn}      , ${bpnCustomer1Name}, ${bpnOwnName}                         , 'fe40ca1c-0d4d-4af0-9ccc-415113f4b7a1', 'DECLINED', current_timestamp - interval '2 days', current_timestamp - interval '12 hours', null      , false     , null);

---
-- join DECLINED notification to asset
 insert into assets_as_built_notification_messages
    (notification_message_id       , asset_id)
values
    (${alertNotificationSentId5b}  , ${assetAsBuiltId17});
