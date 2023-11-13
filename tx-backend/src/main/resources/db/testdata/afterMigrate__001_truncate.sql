-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

-- truncate all tables, except for the "technical" ones (flyway_schema_history and shedlock)
truncate table
    alert,
    alert_notification,
    asset_as_built_alert_notifications,
    asset_as_planned_alert_notifications,
    assets_as_built,
    assets_as_built_alerts,
    assets_as_built_childs,
    assets_as_built_investigations,
    assets_as_built_notifications,
    assets_as_built_parents,
    assets_as_planned,
    assets_as_planned_alerts,
    assets_as_planned_alerts,
    assets_as_planned_childs,
    assets_as_planned_investigations,
    assets_as_built_notifications,
--    bpn_storage,
    investigation,
    investigation_notification;
--    shell_descriptor,
--    submodel,
--    traction_battery_code_subcomponent;

