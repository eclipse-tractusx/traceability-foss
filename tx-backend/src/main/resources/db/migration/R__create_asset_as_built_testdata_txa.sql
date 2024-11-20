DO $$
    DECLARE
        applicationBpn TEXT := '${applicationBpn}';
        bmwBpn TEXT := '${bmwBpn}';
        manufacturerId TEXT;
    BEGIN
        -- Check if the application BPN matches the cofinity BPN
        IF applicationBpn = '${cofinityBpn}' THEN
            -- Assign applicationBpn to manufacturerId
            manufacturerId := applicationBpn;

            -- Perform the insert with manufacturerId set to applicationBpn
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
                                     'HighVoltageBattery1',
                                     'BPNLCOFINITYEZFA',
                                     'Cofinity-X',
                                     '22782277-51',
                                     'DEU',
                                     'High Voltage Battery 1',
                                     'High Voltage Battery 1',
                                     'OK',
                                     'VAN123456',
                                     'OWN',
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
                      'X2 sDrive18d_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS8lQLG',
                      bmwBpn,
                      'BMW',
                      '22782277-50',
                      'DEU',
                      'X2 sDrive18d',
                      'X2 sDrive18d',
                      'OK',
                      'VAN123456',
                      'CUSTOMER',
                      'NO-313869652971440618042264',
                      'SERIALPART',
                      'product',
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

            IF NOT EXISTS (
                SELECT 1 FROM assets_as_built_parents
                WHERE asset_as_built_id = 'urn:uuid:a930fa6d-557f-4eb2-9f36-0a2f53c54fd5'
                  AND id = 'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd'
            ) THEN

            INSERT INTO assets_as_built_parents(
                asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:a930fa6d-557f-4eb2-9f36-0a2f53c54fd5',
                    'urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd',
                    'HighVoltageBattery1');
            END IF;

            --1de

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
                         'HighVoltageBattery2',
                         'BPNLCOFINITYEZFA',
                         'Cofinity-X',
                         '22782277-52',
                         'DEU',
                         'High Voltage Battery 2',
                         'High Voltage Battery 2',
                         'OK',
                         'VAN123456',
                         'OWN',
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
                      'X3 sDrive25d_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS8lQZF',
                      bmwBpn,
                      'BMW',
                      '22782277-51',
                      'DEU',
                      'X3 sDrive25d',
                      'X3 sDrive25d',
                      'OK',
                      'VAN123456',
                      'CUSTOMER',
                      'NO-313869652971440618042264',
                      'SERIALPART',
                      'product',
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
                SELECT 1 FROM assets_as_built_parents
                WHERE asset_as_built_id = 'urn:uuid:24cc69ef-8dd4-44a9-abe3-2fd9e39f61de'
                  AND id = 'urn:uuid:8956d0d4-1252-4ace-8c68-086aac930b7f'
            ) THEN

                INSERT INTO assets_as_built_parents(
                    asset_as_built_id, id, id_short)
                VALUES ('urn:uuid:24cc69ef-8dd4-44a9-abe3-2fd9e39f61de',
                        'urn:uuid:8956d0d4-1252-4ace-8c68-086aac930b7f',
                        'HighVoltageBattery2');
            END IF;

            --16f

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
                         'HighVoltageBattery3',
                         'BPNLCOFINITYEZFA',
                         'Cofinity-X',
                         '22782277-53',
                         'DEU',
                         'High Voltage Battery 3',
                         'High Voltage Battery 3',
                         'OK',
                         'VAN123456',
                         'OWN',
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
                      'X4 M40i_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAE+FS8lQLF',
                      bmwBpn,
                      'BMW',
                      '22782277-52',
                      'DEU',
                      'X4 M40i',
                      'X4 M40i',
                      'OK',
                      'VAN123456',
                      'CUSTOMER',
                      'NO-313869652971440618042264',
                      'SERIALPART',
                      'product',
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
                SELECT 1 FROM assets_as_built_parents
                WHERE asset_as_built_id = 'urn:uuid:0d01bfa9-ea9f-4054-a67d-2e86a8b7c16f'
                  AND id = 'urn:uuid:6dd0e26c-6fcb-49bb-9b37-e6fe40e4e18b'
            ) THEN

                INSERT INTO assets_as_built_parents(
                    asset_as_built_id, id, id_short)
                VALUES ('urn:uuid:0d01bfa9-ea9f-4054-a67d-2e86a8b7c16f',
                        'urn:uuid:6dd0e26c-6fcb-49bb-9b37-e6fe40e4e18b',
                        'HighVoltageBattery3');
            END IF;

            -- urn:uuid:01d8aad4-2b57-4ecf-83ed-24c24ecba8da

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
                         'HighVoltageBattery4',
                         'BPNLCOFINITYEZFA',
                         'Cofinity-X',
                         '22782277-54',
                         'DEU',
                         'High Voltage Battery 4',
                         'High Voltage Battery 4',
                         'OK',
                         'VAN123456',
                         'OWN',
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
                      'M3 Competition xDrive Touring_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjT+EvC8UjQKv0DqAW+FS8lQLF',
                      bmwBpn,
                      'BMW',
                      '22782277-53',
                      'DEU',
                      'M3 Competition M xDrive Touring',
                      'M3 Competition M xDrive Touring',
                      'OK',
                      'VAN123456',
                      'CUSTOMER',
                      'NO-313869652971440618042264',
                      'SERIALPART',
                      'product',
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
                SELECT 1 FROM assets_as_built_parents
                WHERE asset_as_built_id = 'urn:uuid:01d8aad4-2b57-4ecf-83ed-24c24ecba8da'
                  AND id = 'urn:uuid:65f8d0b9-4790-4669-a713-ef6fd05a0b54'
            ) THEN

                INSERT INTO assets_as_built_parents(
                    asset_as_built_id, id, id_short)
                VALUES ('urn:uuid:01d8aad4-2b57-4ecf-83ed-24c24ecba8da',
                        'urn:uuid:65f8d0b9-4790-4669-a713-ef6fd05a0b54',
                        'HighVoltageBattery4');
            END IF;

            --urn:uuid:aba71b24-0af9-482c-8d4b-2048113c55e9

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
                         'HighVoltageBattery5',
                         'BPNLCOFINITYEZFA',
                         'Cofinity-X',
                         '22782277-55',
                         'DEU',
                         'High Voltage Battery 5',
                         'High Voltage Battery 5',
                         'OK',
                         'VAN123456',
                         'OWN',
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
                      'M850i xDrive Cabrio_BPNL000000000ISY_AAIFx6fw5Jf4BJtrN1mtTCqn8QqS4sskyKX872b+o68VrjR+EvC8UjQKv0DqAW+FS9lQLF',
                      bmwBpn,
                      'BMW',
                      '22782277-54',
                      'DEU',
                      'M850i xDrive Cabrio',
                      'M850i xDrive Cabrio',
                      'OK',
                      'VAN123456',
                      'CUSTOMER',
                      'NO-313869652971440618042264',
                      'SERIALPART',
                      'product',
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
                SELECT 1 FROM assets_as_built_parents
                WHERE asset_as_built_id = 'urn:uuid:aba71b24-0af9-482c-8d4b-2048113c55e9'
                  AND id = 'urn:uuid:eca3850f-fa90-4f7a-a151-34df05958621'
            ) THEN

                INSERT INTO assets_as_built_parents(
                    asset_as_built_id, id, id_short)
                VALUES ('urn:uuid:aba71b24-0af9-482c-8d4b-2048113c55e9',
                        'urn:uuid:eca3850f-fa90-4f7a-a151-34df05958621',
                        'HighVoltageBattery5');
            END IF;

        END IF;
    END $$;
