-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into alert
    (id            , bpn               , close_reason, created                              , description                      , status        , side      , accept_reason, decline_reason, updated                              , error_message)
values
    (${alertId1}   , 'BPNL000SUPPLIER1', null        , current_timestamp - interval '2 days', 'Alert about Left O Light Bulbs' , 'ACKNOWLEDGED', 'RECEIVER', null         , null          , current_timestamp - interval '1 days', null),
    (${alertId2}   , 'BPNL000SUPPLIER2', null        , current_timestamp - interval '3 days', 'Alert about Right P Light Bulbs', 'RECEIVED'    , 'RECEIVER', null         , null          , current_timestamp                    , null),
    (${alertId3}   , 'BPNL000000000001', null        , current_timestamp                    , 'Alert about Left Headlights'    , 'CREATED'     , 'SENDER'  , null         , null          , current_timestamp - interval '1 days', null),
    (${alertId4}   , 'BPNL000000000001', null        , current_timestamp                    , 'Test Alert 4'                   , 'SENT'        , 'SENDER'  , null         , null          , current_timestamp                    , null);

select setval('alert_id_seq', (select max(a.id) from alert a), true);
