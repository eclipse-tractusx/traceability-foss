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
                                     'urn:uuid:a930fa6d-557f-4eb2-9f36-0a2f53c54fd5', -- catenaXId
                                     '798-515297795-A',                              -- customerPartId
                                     'HighVoltageBattery1',                                   -- ID Short (unchanged)
                                     'BPNLCOFINITYEZFA',                             -- manufacturerId from localIdentifiers
                                     'Cofinity-X',                                    -- manufacturerName (remains as 'Acme Corp' as not specified)
                                     '22782277-51',                                  -- manufacturerPartId
                                     'DEU',                                          -- manufacturingCountry from manufacturingInformation
                                     'High Voltage Battery 1',                       -- nameAtCustomer
                                     'High Voltage Battery 1',                       -- nameAtManufacturer
                                     'OK',                                           -- qualityType (unchanged)
                                     'VAN123456',                                    -- van (unchanged)
                                     'OWN',                                          -- owner (unchanged)
                                     'NO-613963493493659233961306',                  -- semanticModelId (unchanged)
                                     'SERIALPART',                                   -- semanticDataModel mapped from aspectType
                                     'GIN 20510-21513',                                       -- classification (remains as 'ClassA' as classification info is nested)
                                     'Battery',                                  -- productType (unchanged)
                                     '2022-02-04 14:48:54+00',                       -- manufacturingDate from manufacturingInformation date
                                     'PERSISTENT',                                   -- importState (unchanged)
                                     'Asset successfully created in the system.',    -- importNote (unchanged)
                                     'default-policy',                               -- policyId (unchanged)
                                     NULL,                                           -- tombstone (unchanged)
                                     'CONTRACT_AGR_001',                             -- contractAgreementId (unchanged)
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
            ) VALUES ('urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd', -- catenaXId
                      '22782277-50',                                   -- customerPartId
                      'IDSHORT002',                                    -- ID Short (unchanged)
                      bmwBpn,                              -- manufacturerId from localIdentifiers
                      'Acme Corp',                                     -- manufacturerName (remains as 'Acme Corp' as not specified)
                      '22782277-50',                                   -- manufacturerPartId
                      'DEU',                                           -- manufacturingCountry from manufacturingInformation
                      'Example Car 1',                                 -- nameAtCustomer
                      'Example Car 1',                                 -- nameAtManufacturer
                      'OK',                                            -- qualityType (unchanged)
                      'VAN123456',                                     -- van (unchanged)
                      'CUSTOMER',                                           -- owner (unchanged)
                      'NO-313869652971440618042264',                   -- semanticModelId from partInstanceId in localIdentifiers
                      'SERIALPART',                                    -- semanticDataModel mapped from aspectType
                      'ClassA',                                        -- classification (remains as 'ClassA' as classification info is nested)
                      'Electronics',                                   -- productType (unchanged)
                      '2022-02-04 14:48:54+00',                        -- manufacturingDate from manufacturingInformation date
                      'PERSISTENT',                                    -- importState (unchanged)
                      'Asset successfully created in the system.',     -- importNote (unchanged)
                      'default-policy',                                -- policyId (unchanged)
                      NULL,                                            -- tombstone (unchanged)
                      'CONTRACT_AGR_001',                              -- contractAgreementId (unchanged)
                      'partType'      )
            ON CONFLICT (id) DO NOTHING;

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

        END IF;
    END $$;
