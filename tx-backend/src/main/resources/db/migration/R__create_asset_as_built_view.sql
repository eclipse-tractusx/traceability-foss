-- ${flyway:timestamp}
drop view if exists assets_as_built_view;

create
or replace view assets_as_built_view as
select asset.*,
       (select count(notification.id)
        from notification notification
                 join
             assets_as_built_notifications notification_assets
             on notification.id = notification_assets.notification_id
        where (
                    cast(notification.status as text) = 'CREATED'
                or cast(notification.status as text) = 'SENT'
                or cast(notification.status as text) = 'RECEIVED'
                or cast(notification.status as text) = 'ACKNOWLEDGED'
                or cast(notification.status as text) = 'ACCEPTED'
                or cast(notification.status as text) = 'DECLINED'
            )
          and cast(notification.side as text) = 'RECEIVER'
          and cast(notification.type as text) = 'ALERT'
          and notification_assets.asset_id = asset.id)         as received_active_alerts,
       (select count(notification.id)
        from notification notification
                 join
             assets_as_built_notifications notification_assets
             on notification.id = notification_assets.notification_id
        where (
                    cast(notification.status as text) = 'CREATED'
                or cast(notification.status as text) = 'SENT'
                or cast(notification.status as text) = 'RECEIVED'
                or cast(notification.status as text) = 'ACKNOWLEDGED'
                or cast(notification.status as text) = 'ACCEPTED'
                or cast(notification.status as text) = 'DECLINED'
            )
          and cast(notification.side as text) = 'SENDER'
          and cast(notification.type as text) = 'ALERT'
          and notification_assets.asset_id = asset.id)         as sent_active_alerts,
       (select count(notification.id)
        from notification notification
                 join
             assets_as_built_notifications notification_assets
             on notification.id = notification_assets.notification_id
        where (
                    cast(notification.status as text) = 'CREATED'
                or cast(notification.status as text) = 'SENT'
                or cast(notification.status as text) = 'RECEIVED'
                or cast(notification.status as text) = 'ACKNOWLEDGED'
                or cast(notification.status as text) = 'ACCEPTED'
                or cast(notification.status as text) = 'DECLINED'
            )
          and cast(notification.side as text) = 'RECEIVER'
          and cast(notification.type as text) = 'INVESTIGATION'
          and notification_assets.asset_id = asset.id) as received_active_investigations,
       (select count(notification.id)
        from notification notification
                 join
             assets_as_built_notifications notification_assets
             on notification.id = notification_assets.notification_id
        where (
                    cast(notification.status as text) = 'CREATED'
                or cast(notification.status as text) = 'SENT'
                or cast(notification.status as text) = 'RECEIVED'
                or cast(notification.status as text) = 'ACKNOWLEDGED'
                or cast(notification.status as text) = 'ACCEPTED'
                or cast(notification.status as text) = 'DECLINED'
            )
          and cast(notification.side as text) = 'SENDER'
          and cast(notification.type as text) = 'INVESTIGATION'
          and notification_assets.asset_id = asset.id) as sent_active_investigations
from assets_as_built as asset;
