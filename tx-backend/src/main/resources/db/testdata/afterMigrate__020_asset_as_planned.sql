-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_planned
    (id, id_short, manufacturer_part_id, name_at_manufacturer, quality_type, classification, owner, semantic_data_model, function, manufacturer_name, van, semantic_model_id, catenax_site_id, function_valid_from, function_valid_until, validity_period_from, validity_period_to)
values
    (${assetAsPlannedId1}, '--', 'ManuPartID', 'MyAsPlannedPartName', 'OK', 'C-Level', 'OWN', 'SERIALPART', 'production', 'Audi AG', 'myvan1', 'mySemanticModelId', 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01', '2019-02-04T10:00:00', '2025-02-04T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId2}, '--', 'ManuPartID', 'MyAsPlannedPartName', 'OK', 'C-Level', 'OWN', 'SERIALPART', 'production', 'Audi AG', 'myvan1', 'mySemanticModelId', 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01', '2019-02-04T10:00:00', '2025-02-04T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId3}, '--', 'ManuPartID', 'MyAsPlannedPartName', 'OK', 'C-Level', 'SUPPLIER', 'SERIALPART', 'production', 'Audi AG', 'myvan1', 'mySemanticModelId', 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01', '2019-02-04T10:00:00', '2025-02-04T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId4}, '--', 'ManuPartID', 'MyAsPlannedPartName', 'OK', 'C-Level', 'SUPPLIER', 'SERIALPART', 'production', 'Audi AG', 'myvan1', 'mySemanticModelId', 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01', '2019-02-04T10:00:00', '2025-02-04T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId5}, '--', 'ManuPartID', 'MyAsPlannedPartName', 'OK', 'C-Level', 'CUSTOMER', 'SERIALPART', 'production', 'Audi AG', 'myvan1', 'mySemanticModelId', 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01', '2019-02-04T10:00:00', '2025-02-04T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00'),
    (${assetAsPlannedId6}, '--', 'ManuPartID', 'MyAsPlannedPartName', 'OK', 'C-Level', 'CUSTOMER', 'SERIALPART', 'production', 'Audi AG', 'myvan1', 'mySemanticModelId', 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01', '2019-02-04T10:00:00', '2025-02-04T10:00:00', '2022-02-04T08:00:00', '2024-02-04T08:00:00');
