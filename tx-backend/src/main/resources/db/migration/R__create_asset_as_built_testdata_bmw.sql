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
            ) VALUES ('urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd',
                      '22782277-50',
                      'BMW i5 M60_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS8lQLG',
                      bmwBpn,
                      'BMW',
                      '22782277-50',
                      'DEU',
                      'BMW i5 M60',
                      'G61 BEV',
                      'OK',
                      'VAN123456',
                      'OWN',
                      'NO-313869652971440618042264',
                      'SERIALPART',
                      'BEV',
                      'Electronics',
                      '2022-02-04 14:48:54+00',
                      'PERSISTENT',
                      'Asset successfully created in the system.',
                      'default-policy',
                      NULL,
                      'CONTRACT_AGR_001',
                      'partType'      )
            ON CONFLICT (id) DO UPDATE
                SET
                    customer_part_id = EXCLUDED.customer_part_id,
                    id_short = EXCLUDED.id_short,
                    manufacturer_id = EXCLUDED.manufacturer_id,
                    manufacturer_name = EXCLUDED.manufacturer_name,
                    manufacturer_part_id = EXCLUDED.manufacturer_part_id,
                    manufacturing_country = EXCLUDED.manufacturing_country,
                    name_at_customer = EXCLUDED.name_at_customer,
                    name_at_manufacturer = EXCLUDED.name_at_manufacturer,
                    quality_type = EXCLUDED.quality_type,
                    van = EXCLUDED.van,
                    owner = EXCLUDED.owner,
                    semantic_model_id = EXCLUDED.semantic_model_id,
                    semantic_data_model = EXCLUDED.semantic_data_model,
                    classification = EXCLUDED.classification,
                    product_type = EXCLUDED.product_type,
                    manufacturing_date = EXCLUDED.manufacturing_date,
                    import_state = EXCLUDED.import_state,
                    import_note = EXCLUDED.import_note,
                    policy_id = EXCLUDED.policy_id,
                    tombstone = EXCLUDED.tombstone,
                    contract_agreement_id = EXCLUDED.contract_agreement_id,
                    digital_twin_type = EXCLUDED.digital_twin_type;

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
                                     'urn:uuid:a930fa6d-557f-4eb2-9f36-0a2f53c54fd5',
                                     '798-515297795-A',
                                     'High Voltage Battery SE27',
                                     cofinityBpn,
                                     'Cofinity-X',
                                     '22782277-51',
                                     'DEU',
                                     'High Voltage Battery SE27',
                                     '22782277-51',
                                     'OK',
                                     'VAN123456',
                                     'SUPPLIER',
                                     'NO-613963493493659233961306',
                                     'SERIALPART',
                                     'GIN 20510-21513',
                                     'Electronics',
                                     '2022-02-04 14:48:54+00',
                                     'PERSISTENT',
                                     'Asset successfully created in the system.',
                                     'default-policy',
                                     NULL,
                                     'CONTRACT_AGR_001',
                                     'partType'
                                 )

            ON CONFLICT (id) DO UPDATE
                SET
                    customer_part_id = EXCLUDED.customer_part_id,
                    id_short = EXCLUDED.id_short,
                    manufacturer_id = EXCLUDED.manufacturer_id,
                    manufacturer_name = EXCLUDED.manufacturer_name,
                    manufacturer_part_id = EXCLUDED.manufacturer_part_id,
                    manufacturing_country = EXCLUDED.manufacturing_country,
                    name_at_customer = EXCLUDED.name_at_customer,
                    name_at_manufacturer = EXCLUDED.name_at_manufacturer,
                    quality_type = EXCLUDED.quality_type,
                    van = EXCLUDED.van,
                    owner = EXCLUDED.owner,
                    semantic_model_id = EXCLUDED.semantic_model_id,
                    semantic_data_model = EXCLUDED.semantic_data_model,
                    classification = EXCLUDED.classification,
                    product_type = EXCLUDED.product_type,
                    manufacturing_date = EXCLUDED.manufacturing_date,
                    import_state = EXCLUDED.import_state,
                    import_note = EXCLUDED.import_note,
                    policy_id = EXCLUDED.policy_id,
                    tombstone = EXCLUDED.tombstone,
                    contract_agreement_id = EXCLUDED.contract_agreement_id,
                    digital_twin_type = EXCLUDED.digital_twin_type;

            IF NOT EXISTS (
                SELECT 1 FROM assets_as_built_childs
                WHERE asset_as_built_id = 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
                  AND id = 'urn:uuid:a930fa6d-557f-4eb2-9f36-0a2f53c54fd5'
            ) THEN
                -- Perform the insert if id does not exist
                INSERT INTO assets_as_built_childs (
                    asset_as_built_id, id, id_short
                ) VALUES (
                             'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd',
                             'urn:uuid:a930fa6d-557f-4eb2-9f36-0a2f53c54fd5',
                             'BMW i5 M60_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS8lQLG'
                         );
                ELSE
                UPDATE assets_as_built_childs SET id_short = 'BMW i5 M60_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS8lQLG'
                WHERE asset_as_built_id = 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
                  AND id = 'urn:uuid:a930fa6d-557f-4eb2-9f36-0a2f53c54fd5';
            END IF;

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
            ) VALUES ('urn:uuid:8956d0d4-1252-4ace-8c68-086aac930b7f',
                      '22782277-51',
                      'BMW 550e xDrive_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS8lQZF',
                      bmwBpn,
                      'BMW',
                      '22782277-51',
                      'DEU',
                      'BMW 550e xDrive',
                      'G60 PHEV',
                      'OK',
                      'VAN123456',
                      'OWN',
                      'NO-313869652971440618042264',
                      'SERIALPART',
                      'PHEV',
                      'Electronics',
                      '2022-02-04 14:48:54+00',
                      'PERSISTENT',
                      'Asset successfully created in the system.',
                      'default-policy',
                      NULL,
                      'CONTRACT_AGR_001',
                      'partType'      )
            ON CONFLICT (id) DO UPDATE
                SET
                    customer_part_id = EXCLUDED.customer_part_id,
                    id_short = EXCLUDED.id_short,
                    manufacturer_id = EXCLUDED.manufacturer_id,
                    manufacturer_name = EXCLUDED.manufacturer_name,
                    manufacturer_part_id = EXCLUDED.manufacturer_part_id,
                    manufacturing_country = EXCLUDED.manufacturing_country,
                    name_at_customer = EXCLUDED.name_at_customer,
                    name_at_manufacturer = EXCLUDED.name_at_manufacturer,
                    quality_type = EXCLUDED.quality_type,
                    van = EXCLUDED.van,
                    owner = EXCLUDED.owner,
                    semantic_model_id = EXCLUDED.semantic_model_id,
                    semantic_data_model = EXCLUDED.semantic_data_model,
                    classification = EXCLUDED.classification,
                    product_type = EXCLUDED.product_type,
                    manufacturing_date = EXCLUDED.manufacturing_date,
                    import_state = EXCLUDED.import_state,
                    import_note = EXCLUDED.import_note,
                    policy_id = EXCLUDED.policy_id,
                    tombstone = EXCLUDED.tombstone,
                    contract_agreement_id = EXCLUDED.contract_agreement_id,
                    digital_twin_type = EXCLUDED.digital_twin_type;

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

                         'urn:uuid:24cc69ef-8dd4-44a9-abe3-2fd9e39f61de',
                         '798-515297795-A',
                         'High Voltage Battery SP56',
                         cofinityBpn,
                         'Cofinity-X',
                         '22782277-51',
                         'DEU',
                         'High Voltage Battery SP56',
                         '22782277-51',
                         'OK',
                         'VAN123456',
                         'SUPPLIER',
                         'NO-613963493493659233961306',
                         'SERIALPART',
                         'GIN 20510-21513',
                         'Electronics',
                         '2022-02-04 14:48:54+00',
                         'PERSISTENT',
                         'Asset successfully created in the system.',
                         'default-policy',
                         NULL,
                         'CONTRACT_AGR_001',
                         'partType'

                     )

            ON CONFLICT (id) DO UPDATE
                SET
                    customer_part_id = EXCLUDED.customer_part_id,
                    id_short = EXCLUDED.id_short,
                    manufacturer_id = EXCLUDED.manufacturer_id,
                    manufacturer_name = EXCLUDED.manufacturer_name,
                    manufacturer_part_id = EXCLUDED.manufacturer_part_id,
                    manufacturing_country = EXCLUDED.manufacturing_country,
                    name_at_customer = EXCLUDED.name_at_customer,
                    name_at_manufacturer = EXCLUDED.name_at_manufacturer,
                    quality_type = EXCLUDED.quality_type,
                    van = EXCLUDED.van,
                    owner = EXCLUDED.owner,
                    semantic_model_id = EXCLUDED.semantic_model_id,
                    semantic_data_model = EXCLUDED.semantic_data_model,
                    classification = EXCLUDED.classification,
                    product_type = EXCLUDED.product_type,
                    manufacturing_date = EXCLUDED.manufacturing_date,
                    import_state = EXCLUDED.import_state,
                    import_note = EXCLUDED.import_note,
                    policy_id = EXCLUDED.policy_id,
                    tombstone = EXCLUDED.tombstone,
                    contract_agreement_id = EXCLUDED.contract_agreement_id,
                    digital_twin_type = EXCLUDED.digital_twin_type;

            IF NOT EXISTS (
                SELECT 1 FROM assets_as_built_childs
                WHERE asset_as_built_id = 'urn:uuid:8956d0d4-1252-4ace-8c68-086aac930b7f'
                  AND id = 'urn:uuid:24cc69ef-8dd4-44a9-abe3-2fd9e39f61de'
            ) THEN
                -- Perform the insert if id does not exist
                INSERT INTO assets_as_built_childs (
                    asset_as_built_id, id, id_short
                ) VALUES (
                             'urn:uuid:8956d0d4-1252-4ace-8c68-086aac930b7f',
                             'urn:uuid:24cc69ef-8dd4-44a9-abe3-2fd9e39f61de',
                             'BMW 550e xDrive_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS8lQZF'
                         );
            ELSE
                UPDATE assets_as_built_childs SET id_short = 'BMW 550e xDrive_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS8lQZF'
                WHERE asset_as_built_id = 'urn:uuid:8956d0d4-1252-4ace-8c68-086aac930b7f'
                  AND id = 'urn:uuid:24cc69ef-8dd4-44a9-abe3-2fd9e39f61de';
            END IF;

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
            ) VALUES ('urn:uuid:6dd0e26c-6fcb-49bb-9b37-e6fe40e4e18b',
                      '22782277-52',
                      'BMW i7 eDrive50_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAE+FS8lQLF',
                      bmwBpn,
                      'BMW',
                      '22782277-52',
                      'DEU',
                      'BMW i7 eDrive50',
                      'G70 BEV',
                      'OK',
                      'VAN123456',
                      'OWN',
                      'NO-313869652971440618042264',
                      'SERIALPART',
                      'BEV',
                      'Electronics',
                      '2022-02-04 14:48:54+00',
                      'PERSISTENT',
                      'Asset successfully created in the system.',
                      'default-policy',
                      NULL,
                      'CONTRACT_AGR_001',
                      'partType'      )
            ON CONFLICT (id) DO UPDATE
                SET
                    customer_part_id = EXCLUDED.customer_part_id,
                    id_short = EXCLUDED.id_short,
                    manufacturer_id = EXCLUDED.manufacturer_id,
                    manufacturer_name = EXCLUDED.manufacturer_name,
                    manufacturer_part_id = EXCLUDED.manufacturer_part_id,
                    manufacturing_country = EXCLUDED.manufacturing_country,
                    name_at_customer = EXCLUDED.name_at_customer,
                    name_at_manufacturer = EXCLUDED.name_at_manufacturer,
                    quality_type = EXCLUDED.quality_type,
                    van = EXCLUDED.van,
                    owner = EXCLUDED.owner,
                    semantic_model_id = EXCLUDED.semantic_model_id,
                    semantic_data_model = EXCLUDED.semantic_data_model,
                    classification = EXCLUDED.classification,
                    product_type = EXCLUDED.product_type,
                    manufacturing_date = EXCLUDED.manufacturing_date,
                    import_state = EXCLUDED.import_state,
                    import_note = EXCLUDED.import_note,
                    policy_id = EXCLUDED.policy_id,
                    tombstone = EXCLUDED.tombstone,
                    contract_agreement_id = EXCLUDED.contract_agreement_id,
                    digital_twin_type = EXCLUDED.digital_twin_type;

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

                         'urn:uuid:0d01bfa9-ea9f-4054-a67d-2e86a8b7c16f',
                         '798-515297795-A',
                         'High Voltage Battery SE30',
                         cofinityBpn,
                         'Cofinity-X',
                         '22782277-53',
                         'DEU',
                         'High Voltage Battery SE30',
                         '22782277-51',
                         'OK',
                         'VAN123456',
                         'SUPPLIER',
                         'NO-613963493493659233961306',
                         'SERIALPART',
                         'GIN 20510-21513',
                         'Electronics',
                         '2022-02-04 14:48:54+00',
                         'PERSISTENT',
                         'Asset successfully created in the system.',
                         'default-policy',
                         NULL,
                         'CONTRACT_AGR_001',
                         'partType'

                     )

            ON CONFLICT (id) DO UPDATE
                SET
                    customer_part_id = EXCLUDED.customer_part_id,
                    id_short = EXCLUDED.id_short,
                    manufacturer_id = EXCLUDED.manufacturer_id,
                    manufacturer_name = EXCLUDED.manufacturer_name,
                    manufacturer_part_id = EXCLUDED.manufacturer_part_id,
                    manufacturing_country = EXCLUDED.manufacturing_country,
                    name_at_customer = EXCLUDED.name_at_customer,
                    name_at_manufacturer = EXCLUDED.name_at_manufacturer,
                    quality_type = EXCLUDED.quality_type,
                    van = EXCLUDED.van,
                    owner = EXCLUDED.owner,
                    semantic_model_id = EXCLUDED.semantic_model_id,
                    semantic_data_model = EXCLUDED.semantic_data_model,
                    classification = EXCLUDED.classification,
                    product_type = EXCLUDED.product_type,
                    manufacturing_date = EXCLUDED.manufacturing_date,
                    import_state = EXCLUDED.import_state,
                    import_note = EXCLUDED.import_note,
                    policy_id = EXCLUDED.policy_id,
                    tombstone = EXCLUDED.tombstone,
                    contract_agreement_id = EXCLUDED.contract_agreement_id,
                    digital_twin_type = EXCLUDED.digital_twin_type;

            IF NOT EXISTS (
                SELECT 1 FROM assets_as_built_childs
                WHERE asset_as_built_id = 'urn:uuid:6dd0e26c-6fcb-49bb-9b37-e6fe40e4e18b'
                  AND id = 'urn:uuid:0d01bfa9-ea9f-4054-a67d-2e86a8b7c16f'
            ) THEN
                -- Perform the insert if id does not exist
                INSERT INTO assets_as_built_childs (
                    asset_as_built_id, id, id_short
                ) VALUES (
                             'urn:uuid:6dd0e26c-6fcb-49bb-9b37-e6fe40e4e18b',
                             'urn:uuid:0d01bfa9-ea9f-4054-a67d-2e86a8b7c16f',
                             'BMW i7 eDrive50_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAE+FS8lQLF'
                         );
            ELSE
                UPDATE assets_as_built_childs SET id_short = 'BMW i7 eDrive50_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAE+FS8lQLF'
                WHERE asset_as_built_id = 'urn:uuid:6dd0e26c-6fcb-49bb-9b37-e6fe40e4e18b'
                  AND id = 'urn:uuid:0d01bfa9-ea9f-4054-a67d-2e86a8b7c16f';
            END IF;

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
            ) VALUES ('urn:uuid:65f8d0b9-4790-4669-a713-ef6fd05a0b54',
                      '22782277-53',
                      'BMW i7 xDrive60_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjT+EvC8UjQKv0DqAW+FS8lQLF',
                      bmwBpn,
                      'BMW',
                      '22782277-53',
                      'DEU',
                      'BMW i7 xDrive60',
                      'G70 BEV',
                      'OK',
                      'VAN123456',
                      'OWN',
                      'NO-313869652971440618042264',
                      'SERIALPART',
                      'BEV',
                      'Electronics',
                      '2022-02-04 14:48:54+00',
                      'PERSISTENT',
                      'Asset successfully created in the system.',
                      'default-policy',
                      NULL,
                      'CONTRACT_AGR_001',
                      'partType'      )
            ON CONFLICT (id) DO UPDATE
                SET
                    customer_part_id = EXCLUDED.customer_part_id,
                    id_short = EXCLUDED.id_short,
                    manufacturer_id = EXCLUDED.manufacturer_id,
                    manufacturer_name = EXCLUDED.manufacturer_name,
                    manufacturer_part_id = EXCLUDED.manufacturer_part_id,
                    manufacturing_country = EXCLUDED.manufacturing_country,
                    name_at_customer = EXCLUDED.name_at_customer,
                    name_at_manufacturer = EXCLUDED.name_at_manufacturer,
                    quality_type = EXCLUDED.quality_type,
                    van = EXCLUDED.van,
                    owner = EXCLUDED.owner,
                    semantic_model_id = EXCLUDED.semantic_model_id,
                    semantic_data_model = EXCLUDED.semantic_data_model,
                    classification = EXCLUDED.classification,
                    product_type = EXCLUDED.product_type,
                    manufacturing_date = EXCLUDED.manufacturing_date,
                    import_state = EXCLUDED.import_state,
                    import_note = EXCLUDED.import_note,
                    policy_id = EXCLUDED.policy_id,
                    tombstone = EXCLUDED.tombstone,
                    contract_agreement_id = EXCLUDED.contract_agreement_id,
                    digital_twin_type = EXCLUDED.digital_twin_type;


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
                         'urn:uuid:01d8aad4-2b57-4ecf-83ed-24c24ecba8da',
                         '798-515297795-A',
                         'High Voltage Battery SE30',
                         cofinityBpn,
                         'Cofinity-X',
                         '22782277-54',
                         'DEU',
                         'High Voltage Battery SE30',
                         '22782277-51',
                         'OK',
                         'VAN123456',
                         'SUPPLIER',
                         'NO-613963493493659233961306',
                         'SERIALPART',
                         'GIN 20510-21513',
                         'Electronics',
                         '2022-02-04 14:48:54+00',
                         'PERSISTENT',
                         'Asset successfully created in the system.',
                         'default-policy',
                         NULL,
                         'CONTRACT_AGR_001',
                         'partType'
                     )

            ON CONFLICT (id) DO UPDATE
                SET
                    customer_part_id = EXCLUDED.customer_part_id,
                    id_short = EXCLUDED.id_short,
                    manufacturer_id = EXCLUDED.manufacturer_id,
                    manufacturer_name = EXCLUDED.manufacturer_name,
                    manufacturer_part_id = EXCLUDED.manufacturer_part_id,
                    manufacturing_country = EXCLUDED.manufacturing_country,
                    name_at_customer = EXCLUDED.name_at_customer,
                    name_at_manufacturer = EXCLUDED.name_at_manufacturer,
                    quality_type = EXCLUDED.quality_type,
                    van = EXCLUDED.van,
                    owner = EXCLUDED.owner,
                    semantic_model_id = EXCLUDED.semantic_model_id,
                    semantic_data_model = EXCLUDED.semantic_data_model,
                    classification = EXCLUDED.classification,
                    product_type = EXCLUDED.product_type,
                    manufacturing_date = EXCLUDED.manufacturing_date,
                    import_state = EXCLUDED.import_state,
                    import_note = EXCLUDED.import_note,
                    policy_id = EXCLUDED.policy_id,
                    tombstone = EXCLUDED.tombstone,
                    contract_agreement_id = EXCLUDED.contract_agreement_id,
                    digital_twin_type = EXCLUDED.digital_twin_type;

            IF NOT EXISTS (
                SELECT 1 FROM assets_as_built_childs
                WHERE asset_as_built_id = 'urn:uuid:65f8d0b9-4790-4669-a713-ef6fd05a0b54'
                  AND id = 'urn:uuid:01d8aad4-2b57-4ecf-83ed-24c24ecba8da'
            ) THEN
                -- Perform the insert if id does not exist
                INSERT INTO assets_as_built_childs (
                    asset_as_built_id, id, id_short
                ) VALUES (
                             'urn:uuid:65f8d0b9-4790-4669-a713-ef6fd05a0b54',
                             'urn:uuid:01d8aad4-2b57-4ecf-83ed-24c24ecba8da',
                             'BMW i7 xDrive60_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjT+EvC8UjQKv0DqAW+FS8lQLF'
                         );
            ELSE
                UPDATE assets_as_built_childs SET id_short = 'BMW i7 xDrive60_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjT+EvC8UjQKv0DqAW+FS8lQLF'
                WHERE asset_as_built_id = 'urn:uuid:65f8d0b9-4790-4669-a713-ef6fd05a0b54'
                  AND id = 'urn:uuid:01d8aad4-2b57-4ecf-83ed-24c24ecba8da';
            END IF;

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
            ) VALUES ('urn:uuid:eca3850f-fa90-4f7a-a151-34df05958621',
                      '22782277-54',
                      'BMW 750e xDrive_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS9lQLF',
                      bmwBpn,
                      'BMW',
                      '22782277-54',
                      'DEU',
                      'BMW 750e xDrive',
                      'G70 PHEV',
                      'OK',
                      'VAN123456',
                      'OWN',
                      'NO-313869652971440618042264',
                      'SERIALPART',
                      'PHEV',
                      'Electronics',
                      '2022-02-04 14:48:54+00',
                      'PERSISTENT',
                      'Asset successfully created in the system.',
                      'default-policy',
                      NULL,
                      'CONTRACT_AGR_001',
                      'partType'      )
            ON CONFLICT (id) DO UPDATE
                SET
                    customer_part_id = EXCLUDED.customer_part_id,
                    id_short = EXCLUDED.id_short,
                    manufacturer_id = EXCLUDED.manufacturer_id,
                    manufacturer_name = EXCLUDED.manufacturer_name,
                    manufacturer_part_id = EXCLUDED.manufacturer_part_id,
                    manufacturing_country = EXCLUDED.manufacturing_country,
                    name_at_customer = EXCLUDED.name_at_customer,
                    name_at_manufacturer = EXCLUDED.name_at_manufacturer,
                    quality_type = EXCLUDED.quality_type,
                    van = EXCLUDED.van,
                    owner = EXCLUDED.owner,
                    semantic_model_id = EXCLUDED.semantic_model_id,
                    semantic_data_model = EXCLUDED.semantic_data_model,
                    classification = EXCLUDED.classification,
                    product_type = EXCLUDED.product_type,
                    manufacturing_date = EXCLUDED.manufacturing_date,
                    import_state = EXCLUDED.import_state,
                    import_note = EXCLUDED.import_note,
                    policy_id = EXCLUDED.policy_id,
                    tombstone = EXCLUDED.tombstone,
                    contract_agreement_id = EXCLUDED.contract_agreement_id,
                    digital_twin_type = EXCLUDED.digital_twin_type;


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

                         'urn:uuid:aba71b24-0af9-482c-8d4b-2048113c55e9',
                         '798-515297795-A',
                         'High Voltage Battery SP56',
                         cofinityBpn,
                         'Cofinity-X',
                         '22782277-55',
                         'DEU',
                         'High Voltage Battery SP56',
                         '22782277-51',
                         'OK',
                         'VAN123456',
                         'SUPPLIER',
                         'NO-613963493493659233961306',
                         'SERIALPART',
                         'GIN 20510-21513',
                         'Electronics',
                         '2022-02-04 14:48:54+00',
                         'PERSISTENT',
                         'Asset successfully created in the system.',
                         'default-policy',
                         NULL,
                         'CONTRACT_AGR_001',
                         'partType'
                     )

            ON CONFLICT (id) DO UPDATE
                SET
                    customer_part_id = EXCLUDED.customer_part_id,
                    id_short = EXCLUDED.id_short,
                    manufacturer_id = EXCLUDED.manufacturer_id,
                    manufacturer_name = EXCLUDED.manufacturer_name,
                    manufacturer_part_id = EXCLUDED.manufacturer_part_id,
                    manufacturing_country = EXCLUDED.manufacturing_country,
                    name_at_customer = EXCLUDED.name_at_customer,
                    name_at_manufacturer = EXCLUDED.name_at_manufacturer,
                    quality_type = EXCLUDED.quality_type,
                    van = EXCLUDED.van,
                    owner = EXCLUDED.owner,
                    semantic_model_id = EXCLUDED.semantic_model_id,
                    semantic_data_model = EXCLUDED.semantic_data_model,
                    classification = EXCLUDED.classification,
                    product_type = EXCLUDED.product_type,
                    manufacturing_date = EXCLUDED.manufacturing_date,
                    import_state = EXCLUDED.import_state,
                    import_note = EXCLUDED.import_note,
                    policy_id = EXCLUDED.policy_id,
                    tombstone = EXCLUDED.tombstone,
                    contract_agreement_id = EXCLUDED.contract_agreement_id,
                    digital_twin_type = EXCLUDED.digital_twin_type;

            IF NOT EXISTS (
                SELECT 1 FROM assets_as_built_childs
                WHERE asset_as_built_id = 'urn:uuid:eca3850f-fa90-4f7a-a151-34df05958621'
                  AND id = 'urn:uuid:aba71b24-0af9-482c-8d4b-2048113c55e9'
            ) THEN
                -- Perform the insert if id does not exist
                INSERT INTO assets_as_built_childs (
                    asset_as_built_id, id, id_short
                ) VALUES (
                             'urn:uuid:eca3850f-fa90-4f7a-a151-34df05958621',
                             'urn:uuid:aba71b24-0af9-482c-8d4b-2048113c55e9',
                             'BMW 750e xDrive_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS9lQLF'
                         );
            ELSE
                UPDATE assets_as_built_childs SET id_short = 'BMW 750e xDrive_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS9lQLF'
                WHERE asset_as_built_id = 'urn:uuid:eca3850f-fa90-4f7a-a151-34df05958621'
                  AND id = 'urn:uuid:aba71b24-0af9-482c-8d4b-2048113c55e9';
            END IF;

        END IF;
    END $$;
