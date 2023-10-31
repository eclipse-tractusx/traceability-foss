-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into asset_as_built_alert_notifications
    (alert_notification_id  , asset_id)
values
    (${alertNotificationId1}, ${assetAsBuiltId01}),
    (${alertNotificationId3}, ${assetAsBuiltId03});
