create
or replace view assets_as_built_view as
select asset.*,
       (select count(alert.id)
        from alert alert
                 join
             assets_as_built_alerts alert_assets
             on alert.id = alert_assets.alert_id
        where (
                    cast(alert.status as text) = 'CREATED'
                or cast(alert.status as text) = 'SENT'
                or cast(alert.status as text) = 'RECEIVED'
                or cast(alert.status as text) = 'ACKNOWLEDGED'
                or cast(alert.status as text) = 'ACCEPTED'
                or cast(alert.status as text) = 'DECLINED'
            )
          and cast(alert.side as text) = 'RECEIVER'
          and alert_assets.asset_id = asset.id)         as received_active_alerts,
       (select count(alert.id)
        from alert alert
                 join
             assets_as_built_alerts alert_assets
             on alert.id = alert_assets.alert_id
        where (
                    cast(alert.status as text) = 'CREATED'
                or cast(alert.status as text) = 'SENT'
                or cast(alert.status as text) = 'RECEIVED'
                or cast(alert.status as text) = 'ACKNOWLEDGED'
                or cast(alert.status as text) = 'ACCEPTED'
                or cast(alert.status as text) = 'DECLINED'
            )
          and cast(alert.side as text) = 'SENDER'
          and alert_assets.asset_id = asset.id)         as sent_active_alerts,
       (select count(investigation.id)
        from investigation investigation
                 join
             assets_as_built_investigations investigation_assets
             on investigation.id = investigation_assets.investigation_id
        where (
                    cast(investigation.status as text) = 'CREATED'
                or cast(investigation.status as text) = 'SENT'
                or cast(investigation.status as text) = 'RECEIVED'
                or cast(investigation.status as text) = 'ACKNOWLEDGED'
                or cast(investigation.status as text) = 'ACCEPTED'
                or cast(investigation.status as text) = 'DECLINED'
            )
          and cast(investigation.side as text) = 'RECEIVER'
          and investigation_assets.asset_id = asset.id) as received_active_investigations,
       (select count(investigation.id)
        from investigation investigation
                 join
             assets_as_built_investigations investigation_assets
             on investigation.id = investigation_assets.investigation_id
        where (
                    cast(investigation.status as text) = 'CREATED'
                or cast(investigation.status as text) = 'SENT'
                or cast(investigation.status as text) = 'RECEIVED'
                or cast(investigation.status as text) = 'ACKNOWLEDGED'
                or cast(investigation.status as text) = 'ACCEPTED'
                or cast(investigation.status as text) = 'DECLINED'
            )
          and cast(investigation.side as text) = 'SENDER'
          and investigation_assets.asset_id = asset.id) as sent_active_investigations
from assets_as_built as asset;
