-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state ACKNOWLEDGED in Severity Minor for asBuilt asset Xenon Right-Headlights which is sent from BPNL000CUSTOMER1 to BPNL000000000001

---
insert into investigation
    (id                         , bpn            , close_reason, created                              , description                                                  , status        , side      , accept_reason, decline_reason, updated                               , error_message)
values
    (${investigationReceivedId2}, ${bpnCustomer1}, null        , current_timestamp - interval '2 days', 'Investigation on Xenon Right-Headlights due to malfunction.', 'ACKNOWLEDGED', 'RECEIVER', null         , null          , current_timestamp - interval '1 hours', null);

---
-- reset sequence to highest next-val
select setval('investigation_id_seq1', (select max(i.id) from investigation i), true);

---
insert into investigation_notification
    (id                                      , contract_agreement_id, edc_url, notification_reference_id             , created_by     , send_to  , investigation_id           , target_date                           , severity, created_by_name    , send_to_name , edc_notification_id                   , status    , created                              , updated                               , message_id, is_initial)
values
    (${investigationNotificationReceivedId2a}, null                 , null   , 'c422e2a7-d037-499c-9ec2-44e3bc4f5815', ${bpnCustomer1}, ${bpnOwn}, ${investigationReceivedId2}, current_timestamp + interval '1 month', 'MINOR' , ${bpnCustomer1Name}, ${bpnOwnName}, 'c422e2a7-d037-499c-9ec2-44e3bc4f5815', 'RECEIVED', current_timestamp - interval '2 days', current_timestamp - interval '2 hours', null      , false);

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                         , asset_id)
values
    (${investigationNotificationReceivedId2a}, ${assetAsBuiltId02});

---
-- join investigation to asset
insert into assets_as_built_investigations
    (investigation_id           , asset_id)
values
    (${investigationReceivedId2}, ${assetAsBuiltId02});

---
---
-- ACK by receiver notification message
insert into investigation_notification
    (id                                      , contract_agreement_id, edc_url, notification_reference_id, created_by, send_to        , investigation_id           , target_date                           , severity, created_by_name, send_to_name       , edc_notification_id                   , status        , created                                                  , updated                              , message_id, is_initial)
values
    (${investigationNotificationReceivedId2b}, 'contractAgreementId', null   , null                     , ${bpnOwn} , ${bpnCustomer1}, ${investigationReceivedId2}, current_timestamp + interval '1 month', 'MINOR' , ${bpnOwnName}  , ${bpnCustomer1Name}, 'c422e2a7-d037-499c-9ec2-44e3bc4f5815', 'ACKNOWLEDGED', current_timestamp - interval '2 days' + interval '1 hour', current_timestamp - interval '1 hour', null      , false);

---
-- join ACK notification to asset
insert into assets_as_built_notifications
    (notification_id                         , asset_id)
values
    (${investigationNotificationReceivedId2b}, ${assetAsBuiltId02});
