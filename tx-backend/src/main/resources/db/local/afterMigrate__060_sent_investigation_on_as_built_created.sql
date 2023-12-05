-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- This creates an investigation in state CREATED in Severity Critical for asBuilt asset Osram Front Left-AX400 which is sent from BPNL000000000001 to BPNL000SUPPLIER1

---
insert into investigation
    (id                     , bpn               , close_reason, created                              , description                                                           , status   , side    , accept_reason, decline_reason, updated, error_message)
values
    (${investigationSentId1}, 'BPNL000000000001', null        , current_timestamp - interval '1 day' , 'Investigation on Osram Front Left-AX400 due to excessive brightness.', 'CREATED', 'SENDER', null         , null          , null   , null);

---
-- reset sequence to highest next-val
select setval('investigation_id_seq', (select max(i.id) from investigation i), true);

---
insert into investigation_notification
    (id                                 , contract_agreement_id, edc_url, notification_reference_id, send_to           , created_by        , investigation_id       , target_date                          , severity, created_by_name, send_to_name, edc_notification_id                   , status, created                              , updated                              , message_id                            , is_initial)
values
    (${investigationNotificationSentId1}, null                 , null   , null                     , 'BPNL000SUPPLIER1', 'BPNL000000000001', ${investigationSentId1}, current_timestamp + interval '1 week', 2       , 'Hella'        , 'Osram'     , 'c01353db-640a-44c4-9a87-28fa3a950a95', 0     , current_timestamp - interval '1 day' , current_timestamp - interval '1 hour', '71d7cb88-a208-434b-993e-74aeb331fd11', true);

---
-- join investigation to asset
insert into assets_as_built_notifications
    (notification_id                    , asset_id)
values
    (${investigationNotificationSentId1}, ${assetAsBuiltId03});

---
-- join investigation to asset
insert into assets_as_built_investigations
    (investigation_id       , asset_id)
values
    (${investigationSentId1}, ${assetAsBuiltId03});

---
update assets_as_built
    set in_investigation = true
    where id in (${assetAsBuiltId03});
