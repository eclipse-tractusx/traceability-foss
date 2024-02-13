-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state RECEIVED in Severity Major for asBuilt asset Xenon Left-Headlights which is sent from BPNL000CUSTOMER1 to BPNL000000000001

---
insert into investigation
    (id                         , bpn            , close_reason, created                             , description                                                 , status    , side      , accept_reason, decline_reason, updated, error_message)
values
    (${investigationReceivedId1}, ${bpnCustomer1}, null        , current_timestamp - interval '1 day', 'Investigation on Xenon Left-Headlights due to malfunction.', 'RECEIVED', 'RECEIVER', null         , null          , null   , null);

---
-- reset sequence to highest next-val
select setval('investigation_id_seq1', (select max(i.id) from investigation i), true);

---
insert into investigation_notification
    (id                                     , contract_agreement_id, edc_url, notification_reference_id             , created_by     , send_to  , investigation_id           , target_date                           , severity, created_by_name, send_to_name, edc_notification_id                   , status    , created                             , updated                               , message_id                            , is_initial)
values
    (${investigationNotificationReceivedId1}, null                 , null   , '8925f21f-09eb-4789-81fb-ec221e9e1561', ${bpnCustomer1}, ${bpnOwn}, ${investigationReceivedId1}, current_timestamp + interval '2 weeks', 'MAJOR' , 'BMW AG'       , 'Hella'     , '8925f21f-09eb-4789-81fb-ec221e9e1561', 'RECEIVED', current_timestamp - interval '1 day', current_timestamp - interval '2 hours', 'e04f75e8-d37b-42e4-8cf7-6127f35f3ed5', false);

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                        , asset_id)
values
    (${investigationNotificationReceivedId1}, ${assetAsBuiltId01});

---
-- join investigation to asset
insert into assets_as_built_investigations
    (investigation_id           , asset_id)
values
    (${investigationReceivedId1}, ${assetAsBuiltId01});
