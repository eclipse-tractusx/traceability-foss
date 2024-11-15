-- ${flyway:timestamp}
-- TODO remove after demo / discovery is working
INSERT INTO assets_as_built (id,
                             id_short,
                             owner,
                             classification,
                             quality_type,
                             manufacturer_part_id,
                             manufacturer_id,
                             manufacturer_name,
                             name_at_manufacturer,
                             semantic_data_model,
                             semantic_model_id,
                             van,
                             import_state,
                             import_note,
                             policy_id,
                             tombstone,
                             manufacturing_date,
                             manufacturing_country,
                             name_at_customer,
                             customer_part_id,
                             product_type,
                             digital_twin_type)
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
        NULL, -- productType
        'partType' -- digitalTwinType
       )
ON CONFLICT (id) DO NOTHING;

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
