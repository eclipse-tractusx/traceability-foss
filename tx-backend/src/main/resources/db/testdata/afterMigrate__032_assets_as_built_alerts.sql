-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_alerts
    (alert_id   , asset_id)
values
    -- side as RECEIVER -> supplier as parent sends an alert on given assets to owner as child
    (${alertId1}, ${assetAsBuiltId03}), -- SUP1 QA on O-LeftBulb
    (${alertId2}, ${assetAsBuiltId06}), -- SUP2 QA on P-RightBulb

    -- side as SENDER -> owner as parent sends an alert on given assets to customer as child
    (${alertId3}, ${assetAsBuiltId01}); -- owner created on H-LeftHeadLight

update assets_as_built
    set active_alert = true
    where id in (
                 ${assetAsBuiltId03}, ${assetAsBuiltId06}, -- incoming
                 ${assetAsBuiltId01} -- outgoing
                );
