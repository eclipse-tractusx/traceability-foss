DO $$
    DECLARE
        applicationBpn TEXT := '${applicationBpn}';
        bmwBpn TEXT := '${bmwBpn}';
        cofinityBpn TEXT := '${cofinityBpn}';
        manufacturerId TEXT;
    BEGIN
        -- Check if the application BPN matches the cofinity BPN
        IF applicationBpn = '${bmwBpn}' THEN
            -- Assign applicationBpn to manufacturerId
            manufacturerId := applicationBpn;

            INSERT INTO assets_as_built (
                id,
                customer_part_id,
                id_short,
                manufacturer_id,
                manufacturer_name,
                manufacturer_part_id,
                manufacturing_country,
                name_at_customer,
                name_at_manufacturer,
                quality_type,
                van,
                owner,
                semantic_model_id,
                semantic_data_model,
                classification,
                product_type,
                manufacturing_date,
                import_state,
                import_note,
                policy_id,
                tombstone,
                contract_agreement_id,
                digital_twin_type
            ) VALUES (
                         'urn:uuid:6b2286cc-26c0-4f98-8a22-092338c31888',
                         '5756987-94',
                         'IDSHORT002',
                         manufacturerId,  -- manufacturer_id gets the value of applicationBpn
                         'Acme Corp',
                         'MANUF_PART_456',
                         'DEU',
                         'Widget Pro',
                         'Widget Mk2',
                         'OK',
                         'VAN123456',
                         'SUPPLIER',
                         'NO-613963493493659233961306',
                         'SERIALPART',
                         'ClassA',
                         'Electronics',
                         '2024-11-13 10:00:00+00',
                         'PERSISTENT',
                         'Asset successfully created in the system.',
                         'default-policy',
                         NULL,
                         'CONTRACT_AGR_001',
                      'partType'
                     )
            ON CONFLICT (id) DO NOTHING;


            INSERT INTO assets_as_built (
                id,
                customer_part_id,
                id_short,
                manufacturer_id,
                manufacturer_name,
                manufacturer_part_id,
                manufacturing_country,
                name_at_customer,
                name_at_manufacturer,
                quality_type,
                van,
                owner,
                semantic_model_id,
                semantic_data_model,
                classification,
                product_type,
                manufacturing_date,
                import_state,
                import_note,
                policy_id,
                tombstone,
                contract_agreement_id,
                digital_twin_type
            ) VALUES (
                         'urn:uuid:6b2296cc-26c0-4f99-8a22-092338c31999',
                         '5756987-94',
                         'IDSHORT001',
                         cofinityBpn,  -- manufacturer_id gets the value of applicationBpn
                         'Acme Corp',
                         'MANUF_PART_456',
                         'DEU',
                         'Widget Pro',
                         'Widget Mk2',
                         'OK',
                         'VAN123456',
                         'OWN',
                         'NO-613963493493659233961306',
                         'SERIALPART',
                         'ClassA',
                         'Electronics',
                         '2024-11-13 10:00:00+00',
                         'PERSISTENT',
                         'Asset successfully created in the system.',
                         'default-policy',
                         NULL,
                         'CONTRACT_AGR_001',
                    'partType'
                     )

            ON CONFLICT (id) DO NOTHING;

            IF NOT EXISTS (
                SELECT 1 FROM assets_as_built_childs
                WHERE asset_as_built_id = 'urn:uuid:6b2286cc-26c0-4f98-8a22-092338c31888'
                  AND id = 'urn:uuid:6b2296cc-26c0-4f99-8a22-092338c31999'
            ) THEN
                -- Perform the insert if id does not exist
                INSERT INTO assets_as_built_childs (
                    asset_as_built_id, id, id_short
                ) VALUES (
                             'urn:uuid:6b2286cc-26c0-4f98-8a22-092338c31888',
                             'urn:uuid:6b2296cc-26c0-4f99-8a22-092338c31999',
                             'IDSHORT001'
                         );
            END IF;


        END IF;
    END $$;
