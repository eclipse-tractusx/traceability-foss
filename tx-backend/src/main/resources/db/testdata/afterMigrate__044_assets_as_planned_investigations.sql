-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_planned_investigations
    (investigation_id   , asset_id)
values
    (${investigationId2}, ${assetAsPlannedId1}),
    (${investigationId4}, ${assetAsPlannedId3});

update assets_as_built
    set in_investigation = true
    where id in (${assetAsPlannedId1}, ${assetAsPlannedId3}); -- incoming and outgoing
