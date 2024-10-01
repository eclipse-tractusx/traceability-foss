-- ${flyway:timestamp}
-- TODO remove after demo / discovery is working
INSERT INTO assets_as_built (id,
                             idShort,
                             owner,
                             classification,
                             qualityType,
                             manufacturerPartId,
                             manufacturerId,
                             manufacturerName,
                             nameAtManufacturer,
                             semanticDataModel,
                             semanticModelId,
                             van,
                             importState,
                             importNote,
                             policyId,
                             tombstone,
                             manufacturingDate,
                             manufacturingCountry,
                             nameAtCustomer,
                             customerPartId,
                             productType)
VALUES ('urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36e22', -- id
        NULL, -- idShort
        'SUPPLIER', -- owner (Enum)
        NULL, -- classification
        NULL, -- qualityType (Enum)
        '3500076-05', -- manufacturerPartId
        'BPNL000000002BR4', -- manufacturerId
        NULL, -- manufacturerName
        'a/dev Vehicle Hybrid', -- nameAtManufacturer
        'SERIALPART', -- semanticDataModel (Enum)
        'OMAOYGBDTSRCMYSCX', -- semanticModelId
        'OMAOYGBDTSRCMYSCX', -- van
        'PERSISTENT', -- importState (Enum)
        NULL, -- importNote
        NULL, -- policyId
        NULL, -- tombstone
        '2018-09-28T04:15:57.000Z', -- manufacturingDate (Instant)
        'DEU', -- manufacturingCountry
        NULL, -- nameAtCustomer
        NULL, -- customerPartId
        NULL -- productType
       )
ON CONFLICT (id) DO NOTHING;

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
