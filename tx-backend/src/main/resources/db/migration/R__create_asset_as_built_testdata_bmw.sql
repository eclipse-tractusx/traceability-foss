DO $$
    DECLARE
        applicationBpn TEXT := '${applicationBpn}';
        bpnB TEXT := '${bpnB}';
        bpnA TEXT := '${bpnA}';
    BEGIN
         IF applicationBpn = '${bpnB}' AND '${applyTestData}' = true THEN

            INSERT INTO assets_as_built (
                id, customer_part_id, id_short, manufacturer_id, manufacturer_name, manufacturer_part_id, manufacturing_country, name_at_customer, name_at_manufacturer, quality_type, van, owner, semantic_model_id, semantic_data_model, classification, product_type, manufacturing_date, import_state, import_note, policy_id, tombstone, contract_agreement_id, digital_twin_type
            ) VALUES
                  ('urn:uuid:f0b3dbda-4ffb-4fc2-9b97-c939043de195', '8846473', 'f0b3dbda-4ffb-4fc2-9b97-c939043de195', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143087362515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:a3d25313-c35f-400f-aac0-1802440bcd8a', '8846473', 'a3d25313-c35f-400f-aac0-1802440bcd8a', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143031162515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:5a67ac04-beb8-497a-862c-0bfa8b76d5fe', '8846472', '5a67ac04-beb8-497a-862c-0bfa8b76d5fe', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02443017862515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:ecc7422e-9546-475f-abb2-a1513224f8aa', '8846473', 'ecc7422e-9546-475f-abb2-a1513224f8aa', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02242070462515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:9ecdd941-0012-41b4-8b2c-0189507d8dbf', '8846471', '9ecdd941-0012-41b4-8b2c-0189507d8dbf', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02443011662515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:7977a75e-5d8d-427a-810d-7c6157afac66', '8846473', '7977a75e-5d8d-427a-810d-7c6157afac66', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02242074662515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:43660301-90c5-406f-a16e-95bc24131db8', '8846473', '43660301-90c5-406f-a16e-95bc24131db8', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01543081862515271', 'SERIALPART', NULL, NULL, '2025-01-15T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:9ce0e54e-e069-4c3c-bae3-6451b1bb1d24', '8846473', '9ce0e54e-e069-4c3c-bae3-6451b1bb1d24', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02242075862515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:059ec4a4-be81-41c4-b898-131d8edaa62d', '8846473', '059ec4a4-be81-41c4-b898-131d8edaa62d', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01642031862515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:20469b3e-84f3-41dd-9cfc-331ad57f1af4', '8846471', '20469b3e-84f3-41dd-9cfc-331ad57f1af4', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02443034862515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:ae399247-404f-40af-b8f4-9aeb307f6172', '8846473', 'ae399247-404f-40af-b8f4-9aeb307f6172', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02243049262515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:baa271b7-b185-4cae-8b43-358c78edf786', '8846473', 'baa271b7-b185-4cae-8b43-358c78edf786', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01642038162515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:7a31a848-d224-4606-9e22-081352fea6da', '8846473', '7a31a848-d224-4606-9e22-081352fea6da', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02243048862515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:110a51cf-24d9-452e-8737-5465a7596a7c', '8846473', '110a51cf-24d9-452e-8737-5465a7596a7c', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02242076162515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:cbc52ac9-c783-4368-ab50-09b1322e0197', '8846473', 'cbc52ac9-c783-4368-ab50-09b1322e0197', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01042004062515271', 'SERIALPART', NULL, NULL, '2025-01-10T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:c32b38d7-60eb-469c-a553-3d25f5e3333c', '8846473', 'c32b38d7-60eb-469c-a553-3d25f5e3333c', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01642042962515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:8b7254a2-e31c-416e-8d0d-03e5d8524c6b', '8846473', '8b7254a2-e31c-416e-8d0d-03e5d8524c6b', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143030162515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:a25f5643-d6ea-436d-b3f6-1e236acbfdbe', '8846473', 'a25f5643-d6ea-436d-b3f6-1e236acbfdbe', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01642040662515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:3a86b5ce-9520-42f5-bf65-24a1f8640772', '8846473', '3a86b5ce-9520-42f5-bf65-24a1f8640772', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01642022662515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:dd8f0128-0054-4aa4-ba55-72addbbe7451', '8846473', 'dd8f0128-0054-4aa4-ba55-72addbbe7451', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143087062515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:597e93b5-b1ff-404a-ba51-5acc2042970e', '8846473', '597e93b5-b1ff-404a-ba51-5acc2042970e', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143034762515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:8d9b19df-8ac1-4836-8f9d-fd554d044c2d', '8846473', '8d9b19df-8ac1-4836-8f9d-fd554d044c2d', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02242075562515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:1131e656-db10-487a-a689-df00700836c3', '8846471', '1131e656-db10-487a-a689-df00700836c3', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02643002062515271', 'SERIALPART', NULL, NULL, '2025-01-26T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:167847d0-9d47-4fa9-b745-17e329a74d13', '8846473', '167847d0-9d47-4fa9-b745-17e329a74d13', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02243045062515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:412ae753-71d0-4018-bace-23b2f5cebe04', '8846473', '412ae753-71d0-4018-bace-23b2f5cebe04', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02243045362515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:1bbf4474-38bb-4dca-abaa-89a5ad04176f', '8846472', '1bbf4474-38bb-4dca-abaa-89a5ad04176f', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02443011462515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:9751cc2b-d2b1-421d-9c1a-f8acc37540fd', '8846471', '9751cc2b-d2b1-421d-9c1a-f8acc37540fd', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02643002462515271', 'SERIALPART', NULL, NULL, '2025-01-26T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:45db6770-3a6e-413f-8644-76b7af8f1172', '8846471', '45db6770-3a6e-413f-8644-76b7af8f1172', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02243093262515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:caaace62-9b9d-4791-ac8a-283fc84ce393', '8846471', 'caaace62-9b9d-4791-ac8a-283fc84ce393', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02443018762515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:9c1a3264-7ad7-4356-805c-e233f882d14e', '8846471', '9c1a3264-7ad7-4356-805c-e233f882d14e', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02643001962515271', 'SERIALPART', NULL, NULL, '2025-01-26T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:a8fa8f18-4956-4686-b3fb-a6f1c67940d4', '8846473', 'a8fa8f18-4956-4686-b3fb-a6f1c67940d4', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01043003962515271', 'SERIALPART', NULL, NULL, '2025-01-10T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:6cb80544-9740-46ef-b196-d06d1988153a', '8846471', '6cb80544-9740-46ef-b196-d06d1988153a', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02443018462515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:c82f5ddd-a237-4c79-bfba-62b63ac4f9f3', '8846472', 'c82f5ddd-a237-4c79-bfba-62b63ac4f9f3', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02343022762515271', 'SERIALPART', NULL, NULL, '2025-01-23T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:8b14245d-5790-444f-bdcc-601a844ee4fd', '8846473', '8b14245d-5790-444f-bdcc-601a844ee4fd', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02243049062515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:bf7581f7-eb6c-442b-93f8-3dbfe9f16407', '8846473', 'bf7581f7-eb6c-442b-93f8-3dbfe9f16407', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02243048962515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:118bf045-2598-499c-92f0-e3204827ca20', '8846473', '118bf045-2598-499c-92f0-e3204827ca20', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143086962515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:9abd269a-fbd0-4a6a-aa7a-21edb06a5451', '8846473', '9abd269a-fbd0-4a6a-aa7a-21edb06a5451', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01642039662515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:5da813a2-ab39-41a1-b29c-af9c9bca58b5', '8846472', '5da813a2-ab39-41a1-b29c-af9c9bca58b5', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02443033862515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:0dfe5b22-79b7-4e3e-b8ad-6f51e092ffd7', '8846473', '0dfe5b22-79b7-4e3e-b8ad-6f51e092ffd7', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01642031962515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:e0502a8d-edac-4df8-ad25-720262175157', '8846472', 'e0502a8d-edac-4df8-ad25-720262175157', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02643001862515271', 'SERIALPART', NULL, NULL, '2025-01-26T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:3857b6e1-5094-435b-924f-44af4c9eead4', '8846473', '3857b6e1-5094-435b-924f-44af4c9eead4', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143089562515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:86a0031c-c349-4b0e-8681-cd7c1e820b7c', '8846472', '86a0031c-c349-4b0e-8681-cd7c1e820b7c', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02643001762515271', 'SERIALPART', NULL, NULL, '2025-01-26T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:f6a1fbe1-7820-4e67-92c7-803c3ca3105e', '8846473', 'f6a1fbe1-7820-4e67-92c7-803c3ca3105e', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01642024362515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:77ea2d2e-e13f-4e4e-bc06-884c649a5c29', '8846473', '77ea2d2e-e13f-4e4e-bc06-884c649a5c29', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02242075162515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:24812aa1-8c41-4020-bf8f-796f297d8078', '8846472', '24812aa1-8c41-4020-bf8f-796f297d8078', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02243093062515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:e86e73a3-9c76-4c72-a9ae-bbc384907e4f', '8846472', 'e86e73a3-9c76-4c72-a9ae-bbc384907e4f', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02343023962515271', 'SERIALPART', NULL, NULL, '2025-01-23T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:af8ce1fb-0c2c-4b80-bd20-7b2bba6bd8ee', '8846473', 'af8ce1fb-0c2c-4b80-bd20-7b2bba6bd8ee', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01543086562515271', 'SERIALPART', NULL, NULL, '2025-01-15T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:964aa964-2006-42a6-a2a2-568bfd396204', '8846473', '964aa964-2006-42a6-a2a2-568bfd396204', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143043562515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:630b14ae-23b9-4b73-88b5-fae109633c79', '8846472', '630b14ae-23b9-4b73-88b5-fae109633c79', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02443012162515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:d50dbcdd-6cb1-4829-8ec5-ac8f2ee7ab31', '8846473', 'd50dbcdd-6cb1-4829-8ec5-ac8f2ee7ab31', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01043003862515271', 'SERIALPART', NULL, NULL, '2025-01-10T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:b9afbdd8-5a1b-4688-9b43-0a8f12c1053a', '8846472', 'b9afbdd8-5a1b-4688-9b43-0a8f12c1053a', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02643002962515271', 'SERIALPART', NULL, NULL, '2025-01-26T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:45f9d032-b582-4da3-a517-a3a339a6d3de', '8846473', '45f9d032-b582-4da3-a517-a3a339a6d3de', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02242075962515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:847a7145-0ee7-4b68-88f2-f96a40437bc4', '8846473', '847a7145-0ee7-4b68-88f2-f96a40437bc4', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143086262515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:f1c427c4-48fe-4801-a8f7-8cecb22bf5ac', '8846473', 'f1c427c4-48fe-4801-a8f7-8cecb22bf5ac', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143030262515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:18533875-a398-456b-a4bd-29884f97dee8', '8846471', '18533875-a398-456b-a4bd-29884f97dee8', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02343028162515271', 'SERIALPART', NULL, NULL, '2025-01-23T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:770dc5fa-7649-4d2b-952a-718b65883bcd', '8846473', '770dc5fa-7649-4d2b-952a-718b65883bcd', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143047162515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:aaf37dd0-eb97-4b84-b6f4-3824edaba336', '8846473', 'aaf37dd0-eb97-4b84-b6f4-3824edaba336', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143088262515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:445f47db-1b53-401f-b1c0-49f40eaf6c56', '8846471', '445f47db-1b53-401f-b1c0-49f40eaf6c56', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02443008362515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:3d2ceec7-ae45-4f48-a657-2186bbf0382a', '8846471', '3d2ceec7-ae45-4f48-a657-2186bbf0382a', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02343019662515271', 'SERIALPART', NULL, NULL, '2025-01-23T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:0f88d1d2-064c-4cf7-87ca-745acb5e07e2', '8846473', '0f88d1d2-064c-4cf7-87ca-745acb5e07e2', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143087762515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:558a63f2-0ab5-4917-88b9-6e9c646ab3ba', '8846471', '558a63f2-0ab5-4917-88b9-6e9c646ab3ba', bpnA, 'Cofinity', '8846471', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR1', 'OK', NULL, 'SUPPLIER', 'T588464710425B02643002862515271', 'SERIALPART', NULL, NULL, '2025-01-26T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:e1e3b4ca-3c5b-4602-a0e1-d74756747975', '8846473', 'e1e3b4ca-3c5b-4602-a0e1-d74756747975', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02242074762515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:bb5d6cb0-8ee2-43c0-958a-f011444ad085', '8846473', 'bb5d6cb0-8ee2-43c0-958a-f011444ad085', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02243049362515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:8aeb0568-b226-48c1-a182-91d7d2fce440', '8846472', '8aeb0568-b226-48c1-a182-91d7d2fce440', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02443018262515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:3463735b-5d4c-4832-a87f-7161448d8063', '8846472', '3463735b-5d4c-4832-a87f-7161448d8063', bpnA, 'Cofinity', '8846472', 'DEU', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'ZZ ZB EM 5S5P PHEV2 60AH LF2 HS VAR2', 'OK', NULL, 'SUPPLIER', 'T588464720425B02643002262515271', 'SERIALPART', NULL, NULL, '2025-01-26T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:e9a88bef-27ec-4353-804a-08c4cabe2d23', '8846473', 'e9a88bef-27ec-4353-804a-08c4cabe2d23', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02143087862515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:62336610-d29b-40a4-9d59-83271bce9283', '8846473', '62336610-d29b-40a4-9d59-83271bce9283', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B02242074062515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:be1eabe9-89d5-4406-b443-41937127934c', '8846473', 'be1eabe9-89d5-4406-b443-41937127934c', bpnA, 'Cofinity', '8846473', 'DEU', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'ZZ ZB EM 4S5P PHEV2 60AH LF2 HS', 'OK', NULL, 'SUPPLIER', 'T588464730425B01042012062515271', 'SERIALPART', NULL, NULL, '2025-01-10T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', '8873876', '53f3371e-81e2-4bea-953e-d683688550cc', bpnB, 'BMW', '8873876', 'DEU', 'ZB LU HVS SE10 AEND AUSLIEFER-SW', 'ZB LU HVS SE10 AEND AUSLIEFER-SW', 'OK', NULL, 'OWN', 'T588738760125B01330037462515271', 'SERIALPART', NULL, NULL, '2025-01-13T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', '8887932', '9a8ab85d-58be-4045-8caa-8fab8fe511e6', bpnB, 'BMW', '8887932', 'DEU', 'ZB LU HVS SE10 US LABEL', 'ZB LU HVS SE10 US LABEL', 'OK', NULL, 'OWN', 'T588879320125B03130040662515271', 'SERIALPART', NULL, NULL, '2025-01-31T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', '8887932', '4d2da80a-f483-4c2b-8207-8d794079dee7', bpnB, 'BMW', '8887932', 'DEU', 'ZB LU HVS SE10 US LABEL', 'ZB LU HVS SE10 US LABEL', 'OK', NULL, 'OWN', 'T588879320125B03130040462515271', 'SERIALPART', NULL, NULL, '2025-01-31T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', '8887932', '393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', bpnB, 'BMW', '8887932', 'DEU', 'ZB LU HVS SE10 US LABEL', 'ZB LU HVS SE10 US LABEL', 'OK', NULL, 'OWN', 'T588879320125B03130043362515271', 'SERIALPART', NULL, NULL, '2025-01-31T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', '8873876', '78673ce4-9037-4f34-9014-9797080e768e', bpnB, 'BMW', '8873876', 'DEU', 'ZB LU HVS SE10 AEND AUSLIEFER-SW', 'ZB LU HVS SE10 AEND AUSLIEFER-SW', 'OK', NULL, 'OWN', 'T588738760125B02730055062515271', 'SERIALPART', NULL, NULL, '2025-01-27T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61', '8886707', '1e3f162e-89db-4be8-8882-8caa26122c61', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B01040037162515271', 'SERIALPART', NULL, NULL, '2025-01-10T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59', '8886708', 'c97bf26e-c91e-48f1-8301-f90ec1087f59', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02740021362515271', 'SERIALPART', NULL, NULL, '2025-01-27T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:2be97c42-4549-473d-bba9-4be8138b8ae4', '8886708', '2be97c42-4549-473d-bba9-4be8138b8ae4', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080124B34740099162515271', 'SERIALPART', NULL, NULL, '2024-12-12T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc', '8886707', '9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02240109762515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e', '8886707', '318a53e9-0871-48f9-9e2e-77075960335e', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02240110462515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3', '8886708', '30870315-6a8e-4f7f-9071-cde29acf7ca3', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02540000162515271', 'SERIALPART', NULL, NULL, '2025-01-25T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542', '8886708', '338598c7-a1fb-4568-9e5b-7a1bc7d27542', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02340074262515271', 'SERIALPART', NULL, NULL, '2025-01-23T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d', '8886707', 'e09ffbaf-183c-4089-a256-f6e23cd1462d', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B01640054162515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3', '8886707', 'd281057e-63a6-4d93-926b-08c1d75db7a3', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B01040036662515271', 'SERIALPART', NULL, NULL, '2025-01-10T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7', '8886707', 'f9e9754f-191d-4b01-a53b-0b209384eff7', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02240110362515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced', '8886707', '60b573f5-8ae0-4e76-9577-cf946f9ceced', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B01640082362515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4', '8886707', '09463eab-f4e9-4b27-b0f9-b9e82fe47bf4', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02240109362515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:734937a2-a242-4a8f-8dbe-a9c281e080aa', '8886707', '734937a2-a242-4a8f-8dbe-a9c281e080aa', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070124B35140048362515271', 'SERIALPART', NULL, NULL, '2024-12-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e', '8886708', '2495ca87-4799-4788-ab0f-ab05dc828a7e', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02740020262515271', 'SERIALPART', NULL, NULL, '2025-01-27T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389', '8886707', '20bf29c8-e467-44ed-bbf6-d9b24637e389', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02140070162515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9', '8886708', '482818a8-2646-45a0-8de4-579685efd8c9', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02340022462515271', 'SERIALPART', NULL, NULL, '2025-01-23T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d', '8886707', 'd9df9966-4da5-42f9-8924-0946d0631b7d', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02140058062515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:546332ed-e620-40e1-8166-9d82422431f7', '8886708', '546332ed-e620-40e1-8166-9d82422431f7', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02740022762515271', 'SERIALPART', NULL, NULL, '2025-01-27T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:d71846a2-58f4-46a4-80f0-c4904b47e167', '8886708', 'd71846a2-58f4-46a4-80f0-c4904b47e167', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080124B34740098162515271', 'SERIALPART', NULL, NULL, '2024-12-12T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:1553d4ad-273a-4593-8539-73f09d674126', '8886708', '1553d4ad-273a-4593-8539-73f09d674126', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02440037062515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732', '8886707', 'fc302fe1-18ab-4f6a-90b3-60fb9e387732', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02140137462515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b', '8886707', 'db4f17d6-ec55-4dd9-9bf8-c82855f47e5b', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02140136362515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039', '8886707', '028e3a8e-a71b-4743-817c-e3ec5e941039', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02140136262515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547', '8886707', 'b687c70b-2e42-4aa4-9bd3-b01b6d916547', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02140057062515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d', '8886708', 'd5a9fbff-708c-4bbe-93d8-01f16864513d', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02440040062515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b', '8886707', 'e2bb4585-2df8-46e2-8cd1-3aa3b79e372b', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B01640080062515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e', '8886707', '34d5a28c-d875-4980-90b9-88a11ed53e5e', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02240109962515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40', '8886707', '0345a5e3-5c0f-43a1-89f7-b07d0255ba40', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02240105362515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659', '8886708', '55c10558-471b-4a0c-b453-de3ff0bee659', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02440040262515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74', '8886707', 'f6e33818-e515-4882-b77e-dbb5bb5c8a74', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02240102662515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:a97230c5-41aa-404d-a843-25adcbcc7a0d', '8886707', 'a97230c5-41aa-404d-a843-25adcbcc7a0d', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070124B35140048562515271', 'SERIALPART', NULL, NULL, '2024-12-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e', '8886708', 'fccbd7ba-035f-4fad-b905-a8a71c3f462e', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02340068862515271', 'SERIALPART', NULL, NULL, '2025-01-23T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a', '8886708', 'f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02740020162515271', 'SERIALPART', NULL, NULL, '2025-01-27T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d', '8886707', 'd4a8f709-aa45-475f-9602-018127b0d36d', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B01640068562515271', 'SERIALPART', NULL, NULL, '2025-01-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:6bb155bb-1e2a-4928-9b6e-ecf2d3bfc256', '8886708', '6bb155bb-1e2a-4928-9b6e-ecf2d3bfc256', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080124B35140141862515271', 'SERIALPART', NULL, NULL, '2024-12-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596', '8886707', '9d6d4fd3-a86a-45eb-9ddf-1455eda98596', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02240110062515271', 'SERIALPART', NULL, NULL, '2025-01-22T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:5de9e7a6-0cbd-46c9-955c-931fa3fc9438', '8886707', '5de9e7a6-0cbd-46c9-955c-931fa3fc9438', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070124B35140047162515271', 'SERIALPART', NULL, NULL, '2024-12-16T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228', '8886707', '66187ac6-3eb4-4fb8-abea-dabbbcc21228', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B01540155862515271', 'SERIALPART', NULL, NULL, '2025-01-15T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6', '8886708', 'fed26bb0-4ca5-46b1-b36e-d1d800c54af6', bpnB, 'BMW', '8886708', 'DEU', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'ZB DM 10S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867080125B02440037762515271', 'SERIALPART', NULL, NULL, '2025-01-24T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType'),
                  ('urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca', '8886707', 'f36ab678-65ab-4dd2-8b33-edb2d2745aca', bpnB, 'BMW', '8886707', 'DEU', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'ZB DM 8S5P PHEV2 HEATSHIELD GR2', 'OK', NULL, 'OWN', 'T588867070125B02140134862515271', 'SERIALPART', NULL, NULL, '2025-01-21T00:00:00.000Z', 'PERSISTENT', 'Asset successfully created in the system.', 'default_policy', NULL, 'CONTRACT_AGR_001', 'partType')
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
            WHERE asset_as_built_id = 'urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74'
              AND id = 'urn:uuid:412ae753-71d0-4018-bace-23b2f5cebe04'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74', 'urn:uuid:412ae753-71d0-4018-bace-23b2f5cebe04', '412ae753-71d0-4018-bace-23b2f5cebe04');
        END IF;


        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74'
              AND id = 'urn:uuid:167847d0-9d47-4fa9-b745-17e329a74d13'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74', 'urn:uuid:167847d0-9d47-4fa9-b745-17e329a74d13', '167847d0-9d47-4fa9-b745-17e329a74d13');
        END IF;


        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4'
              AND id = 'urn:uuid:7977a75e-5d8d-427a-810d-7c6157afac66'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4', 'urn:uuid:7977a75e-5d8d-427a-810d-7c6157afac66', '7977a75e-5d8d-427a-810d-7c6157afac66');
        END IF;


        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4'
              AND id = 'urn:uuid:e1e3b4ca-3c5b-4602-a0e1-d74756747975'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4', 'urn:uuid:e1e3b4ca-3c5b-4602-a0e1-d74756747975', 'e1e3b4ca-3c5b-4602-a0e1-d74756747975');
        END IF;


        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7'
              AND id = 'urn:uuid:45f9d032-b582-4da3-a517-a3a339a6d3de'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7', 'urn:uuid:45f9d032-b582-4da3-a517-a3a339a6d3de', '45f9d032-b582-4da3-a517-a3a339a6d3de');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7'
              AND id = 'urn:uuid:ae399247-404f-40af-b8f4-9aeb307f6172'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7', 'urn:uuid:ae399247-404f-40af-b8f4-9aeb307f6172', 'ae399247-404f-40af-b8f4-9aeb307f6172');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61'
              AND id = 'urn:uuid:cbc52ac9-c783-4368-ab50-09b1322e0197'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61', 'urn:uuid:cbc52ac9-c783-4368-ab50-09b1322e0197', 'cbc52ac9-c783-4368-ab50-09b1322e0197');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61'
              AND id = 'urn:uuid:a8fa8f18-4956-4686-b3fb-a6f1c67940d4'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61', 'urn:uuid:a8fa8f18-4956-4686-b3fb-a6f1c67940d4', 'a8fa8f18-4956-4686-b3fb-a6f1c67940d4');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6'
              AND id = 'urn:uuid:1bbf4474-38bb-4dca-abaa-89a5ad04176f'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6', 'urn:uuid:1bbf4474-38bb-4dca-abaa-89a5ad04176f', '1bbf4474-38bb-4dca-abaa-89a5ad04176f');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6'
              AND id = 'urn:uuid:9ecdd941-0012-41b4-8b2c-0189507d8dbf'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6', 'urn:uuid:9ecdd941-0012-41b4-8b2c-0189507d8dbf', '9ecdd941-0012-41b4-8b2c-0189507d8dbf');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547'
              AND id = 'urn:uuid:f1c427c4-48fe-4801-a8f7-8cecb22bf5ac'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547', 'urn:uuid:f1c427c4-48fe-4801-a8f7-8cecb22bf5ac', 'f1c427c4-48fe-4801-a8f7-8cecb22bf5ac');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547'
              AND id = 'urn:uuid:8b7254a2-e31c-416e-8d0d-03e5d8524c6b'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547', 'urn:uuid:8b7254a2-e31c-416e-8d0d-03e5d8524c6b', '8b7254a2-e31c-416e-8d0d-03e5d8524c6b');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039'
              AND id = 'urn:uuid:dd8f0128-0054-4aa4-ba55-72addbbe7451'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039', 'urn:uuid:dd8f0128-0054-4aa4-ba55-72addbbe7451', 'dd8f0128-0054-4aa4-ba55-72addbbe7451');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039'
              AND id = 'urn:uuid:118bf045-2598-499c-92f0-e3204827ca20'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039', 'urn:uuid:118bf045-2598-499c-92f0-e3204827ca20', '118bf045-2598-499c-92f0-e3204827ca20');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:546332ed-e620-40e1-8166-9d82422431f7'
              AND id = 'urn:uuid:b9afbdd8-5a1b-4688-9b43-0a8f12c1053a'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:546332ed-e620-40e1-8166-9d82422431f7', 'urn:uuid:b9afbdd8-5a1b-4688-9b43-0a8f12c1053a', 'b9afbdd8-5a1b-4688-9b43-0a8f12c1053a');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:546332ed-e620-40e1-8166-9d82422431f7'
              AND id = 'urn:uuid:558a63f2-0ab5-4917-88b9-6e9c646ab3ba'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:546332ed-e620-40e1-8166-9d82422431f7', 'urn:uuid:558a63f2-0ab5-4917-88b9-6e9c646ab3ba', '558a63f2-0ab5-4917-88b9-6e9c646ab3ba');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e'
              AND id = 'urn:uuid:9ce0e54e-e069-4c3c-bae3-6451b1bb1d24'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e', 'urn:uuid:9ce0e54e-e069-4c3c-bae3-6451b1bb1d24', '9ce0e54e-e069-4c3c-bae3-6451b1bb1d24');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e'
              AND id = 'urn:uuid:bf7581f7-eb6c-442b-93f8-3dbfe9f16407'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e', 'urn:uuid:bf7581f7-eb6c-442b-93f8-3dbfe9f16407', 'bf7581f7-eb6c-442b-93f8-3dbfe9f16407');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e'
              AND id = 'urn:uuid:110a51cf-24d9-452e-8737-5465a7596a7c'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e', 'urn:uuid:110a51cf-24d9-452e-8737-5465a7596a7c', '110a51cf-24d9-452e-8737-5465a7596a7c');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e'
              AND id = 'urn:uuid:bb5d6cb0-8ee2-43c0-958a-f011444ad085'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e', 'urn:uuid:bb5d6cb0-8ee2-43c0-958a-f011444ad085', 'bb5d6cb0-8ee2-43c0-958a-f011444ad085');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659'
              AND id = 'urn:uuid:8aeb0568-b226-48c1-a182-91d7d2fce440'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659', 'urn:uuid:8aeb0568-b226-48c1-a182-91d7d2fce440', '8aeb0568-b226-48c1-a182-91d7d2fce440');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659'
              AND id = 'urn:uuid:caaace62-9b9d-4791-ac8a-283fc84ce393'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659', 'urn:uuid:caaace62-9b9d-4791-ac8a-283fc84ce393', 'caaace62-9b9d-4791-ac8a-283fc84ce393');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e'
              AND id = 'urn:uuid:1131e656-db10-487a-a689-df00700836c3'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e', 'urn:uuid:1131e656-db10-487a-a689-df00700836c3', '1131e656-db10-487a-a689-df00700836c3');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e'
              AND id = 'urn:uuid:e0502a8d-edac-4df8-ad25-720262175157'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e', 'urn:uuid:e0502a8d-edac-4df8-ad25-720262175157', 'e0502a8d-edac-4df8-ad25-720262175157');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc'
              AND id = 'urn:uuid:7a31a848-d224-4606-9e22-081352fea6da'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc', 'urn:uuid:7a31a848-d224-4606-9e22-081352fea6da', '7a31a848-d224-4606-9e22-081352fea6da');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc'
              AND id = 'urn:uuid:77ea2d2e-e13f-4e4e-bc06-884c649a5c29'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc', 'urn:uuid:77ea2d2e-e13f-4e4e-bc06-884c649a5c29', '77ea2d2e-e13f-4e4e-bc06-884c649a5c29');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca'
              AND id = 'urn:uuid:847a7145-0ee7-4b68-88f2-f96a40437bc4'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca', 'urn:uuid:847a7145-0ee7-4b68-88f2-f96a40437bc4', '847a7145-0ee7-4b68-88f2-f96a40437bc4');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca'
              AND id = 'urn:uuid:aaf37dd0-eb97-4b84-b6f4-3824edaba336'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca', 'urn:uuid:aaf37dd0-eb97-4b84-b6f4-3824edaba336', 'aaf37dd0-eb97-4b84-b6f4-3824edaba336');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596'
              AND id = 'urn:uuid:8d9b19df-8ac1-4836-8f9d-fd554d044c2d'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596', 'urn:uuid:8d9b19df-8ac1-4836-8f9d-fd554d044c2d', '8d9b19df-8ac1-4836-8f9d-fd554d044c2d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596'
              AND id = 'urn:uuid:8b14245d-5790-444f-bdcc-601a844ee4fd'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596', 'urn:uuid:8b14245d-5790-444f-bdcc-601a844ee4fd', '8b14245d-5790-444f-bdcc-601a844ee4fd');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389'
              AND id = 'urn:uuid:964aa964-2006-42a6-a2a2-568bfd396204'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389', 'urn:uuid:964aa964-2006-42a6-a2a2-568bfd396204', '964aa964-2006-42a6-a2a2-568bfd396204');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389'
              AND id = 'urn:uuid:770dc5fa-7649-4d2b-952a-718b65883bcd'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389', 'urn:uuid:770dc5fa-7649-4d2b-952a-718b65883bcd', '770dc5fa-7649-4d2b-952a-718b65883bcd');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3'
              AND id = 'urn:uuid:be1eabe9-89d5-4406-b443-41937127934c'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3', 'urn:uuid:be1eabe9-89d5-4406-b443-41937127934c', 'be1eabe9-89d5-4406-b443-41937127934c');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3'
              AND id = 'urn:uuid:d50dbcdd-6cb1-4829-8ec5-ac8f2ee7ab31'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3', 'urn:uuid:d50dbcdd-6cb1-4829-8ec5-ac8f2ee7ab31', 'd50dbcdd-6cb1-4829-8ec5-ac8f2ee7ab31');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced'
              AND id = 'urn:uuid:9abd269a-fbd0-4a6a-aa7a-21edb06a5451'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced', 'urn:uuid:9abd269a-fbd0-4a6a-aa7a-21edb06a5451', '9abd269a-fbd0-4a6a-aa7a-21edb06a5451');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced'
              AND id = 'urn:uuid:a25f5643-d6ea-436d-b3f6-1e236acbfdbe'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced', 'urn:uuid:a25f5643-d6ea-436d-b3f6-1e236acbfdbe', 'a25f5643-d6ea-436d-b3f6-1e236acbfdbe');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3'
              AND id = 'urn:uuid:5da813a2-ab39-41a1-b29c-af9c9bca58b5'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3', 'urn:uuid:5da813a2-ab39-41a1-b29c-af9c9bca58b5', '5da813a2-ab39-41a1-b29c-af9c9bca58b5');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3'
              AND id = 'urn:uuid:20469b3e-84f3-41dd-9cfc-331ad57f1af4'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3', 'urn:uuid:20469b3e-84f3-41dd-9cfc-331ad57f1af4', '20469b3e-84f3-41dd-9cfc-331ad57f1af4');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228'
              AND id = 'urn:uuid:43660301-90c5-406f-a16e-95bc24131db8'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228', 'urn:uuid:43660301-90c5-406f-a16e-95bc24131db8', '43660301-90c5-406f-a16e-95bc24131db8');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228'
              AND id = 'urn:uuid:af8ce1fb-0c2c-4b80-bd20-7b2bba6bd8ee'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228', 'urn:uuid:af8ce1fb-0c2c-4b80-bd20-7b2bba6bd8ee', 'af8ce1fb-0c2c-4b80-bd20-7b2bba6bd8ee');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d'
              AND id = 'urn:uuid:3a86b5ce-9520-42f5-bf65-24a1f8640772'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d', 'urn:uuid:3a86b5ce-9520-42f5-bf65-24a1f8640772', '3a86b5ce-9520-42f5-bf65-24a1f8640772');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d'
              AND id = 'urn:uuid:f6a1fbe1-7820-4e67-92c7-803c3ca3105e'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d', 'urn:uuid:f6a1fbe1-7820-4e67-92c7-803c3ca3105e', 'f6a1fbe1-7820-4e67-92c7-803c3ca3105e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a'
              AND id = 'urn:uuid:9c1a3264-7ad7-4356-805c-e233f882d14e'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a', 'urn:uuid:9c1a3264-7ad7-4356-805c-e233f882d14e', '9c1a3264-7ad7-4356-805c-e233f882d14e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a'
              AND id = 'urn:uuid:86a0031c-c349-4b0e-8681-cd7c1e820b7c'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a', 'urn:uuid:86a0031c-c349-4b0e-8681-cd7c1e820b7c', '86a0031c-c349-4b0e-8681-cd7c1e820b7c');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542'
              AND id = 'urn:uuid:18533875-a398-456b-a4bd-29884f97dee8'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542', 'urn:uuid:18533875-a398-456b-a4bd-29884f97dee8', '18533875-a398-456b-a4bd-29884f97dee8');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542'
              AND id = 'urn:uuid:c82f5ddd-a237-4c79-bfba-62b63ac4f9f3'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542', 'urn:uuid:c82f5ddd-a237-4c79-bfba-62b63ac4f9f3', 'c82f5ddd-a237-4c79-bfba-62b63ac4f9f3');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b'
              AND id = 'urn:uuid:e9a88bef-27ec-4353-804a-08c4cabe2d23'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b', 'urn:uuid:e9a88bef-27ec-4353-804a-08c4cabe2d23', 'e9a88bef-27ec-4353-804a-08c4cabe2d23');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b'
              AND id = 'urn:uuid:f0b3dbda-4ffb-4fc2-9b97-c939043de195'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b', 'urn:uuid:f0b3dbda-4ffb-4fc2-9b97-c939043de195', 'f0b3dbda-4ffb-4fc2-9b97-c939043de195');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b'
              AND id = 'urn:uuid:c32b38d7-60eb-469c-a553-3d25f5e3333c'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b', 'urn:uuid:c32b38d7-60eb-469c-a553-3d25f5e3333c', 'c32b38d7-60eb-469c-a553-3d25f5e3333c');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b'
              AND id = 'urn:uuid:baa271b7-b185-4cae-8b43-358c78edf786'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b', 'urn:uuid:baa271b7-b185-4cae-8b43-358c78edf786', 'baa271b7-b185-4cae-8b43-358c78edf786');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59'
              AND id = 'urn:uuid:3463735b-5d4c-4832-a87f-7161448d8063'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59', 'urn:uuid:3463735b-5d4c-4832-a87f-7161448d8063', '3463735b-5d4c-4832-a87f-7161448d8063');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59'
              AND id = 'urn:uuid:9751cc2b-d2b1-421d-9c1a-f8acc37540fd'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59', 'urn:uuid:9751cc2b-d2b1-421d-9c1a-f8acc37540fd', '9751cc2b-d2b1-421d-9c1a-f8acc37540fd');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9'
              AND id = 'urn:uuid:24812aa1-8c41-4020-bf8f-796f297d8078'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9', 'urn:uuid:24812aa1-8c41-4020-bf8f-796f297d8078', '24812aa1-8c41-4020-bf8f-796f297d8078');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9'
              AND id = 'urn:uuid:45db6770-3a6e-413f-8644-76b7af8f1172'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9', 'urn:uuid:45db6770-3a6e-413f-8644-76b7af8f1172', '45db6770-3a6e-413f-8644-76b7af8f1172');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40'
              AND id = 'urn:uuid:ecc7422e-9546-475f-abb2-a1513224f8aa'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40', 'urn:uuid:ecc7422e-9546-475f-abb2-a1513224f8aa', 'ecc7422e-9546-475f-abb2-a1513224f8aa');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40'
              AND id = 'urn:uuid:62336610-d29b-40a4-9d59-83271bce9283'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40', 'urn:uuid:62336610-d29b-40a4-9d59-83271bce9283', '62336610-d29b-40a4-9d59-83271bce9283');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d'
              AND id = 'urn:uuid:597e93b5-b1ff-404a-ba51-5acc2042970e'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d', 'urn:uuid:597e93b5-b1ff-404a-ba51-5acc2042970e', '597e93b5-b1ff-404a-ba51-5acc2042970e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d'
              AND id = 'urn:uuid:a3d25313-c35f-400f-aac0-1802440bcd8a'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d', 'urn:uuid:a3d25313-c35f-400f-aac0-1802440bcd8a', 'a3d25313-c35f-400f-aac0-1802440bcd8a');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e'
              AND id = 'urn:uuid:e86e73a3-9c76-4c72-a9ae-bbc384907e4f'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e', 'urn:uuid:e86e73a3-9c76-4c72-a9ae-bbc384907e4f', 'e86e73a3-9c76-4c72-a9ae-bbc384907e4f');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e'
              AND id = 'urn:uuid:3d2ceec7-ae45-4f48-a657-2186bbf0382a'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e', 'urn:uuid:3d2ceec7-ae45-4f48-a657-2186bbf0382a', '3d2ceec7-ae45-4f48-a657-2186bbf0382a');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d'
              AND id = 'urn:uuid:059ec4a4-be81-41c4-b898-131d8edaa62d'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d', 'urn:uuid:059ec4a4-be81-41c4-b898-131d8edaa62d', '059ec4a4-be81-41c4-b898-131d8edaa62d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d'
              AND id = 'urn:uuid:0dfe5b22-79b7-4e3e-b8ad-6f51e092ffd7'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d', 'urn:uuid:0dfe5b22-79b7-4e3e-b8ad-6f51e092ffd7', '0dfe5b22-79b7-4e3e-b8ad-6f51e092ffd7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d'
              AND id = 'urn:uuid:5a67ac04-beb8-497a-862c-0bfa8b76d5fe'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d', 'urn:uuid:5a67ac04-beb8-497a-862c-0bfa8b76d5fe', '5a67ac04-beb8-497a-862c-0bfa8b76d5fe');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d'
              AND id = 'urn:uuid:6cb80544-9740-46ef-b196-d06d1988153a'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d', 'urn:uuid:6cb80544-9740-46ef-b196-d06d1988153a', '6cb80544-9740-46ef-b196-d06d1988153a');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732'
              AND id = 'urn:uuid:0f88d1d2-064c-4cf7-87ca-745acb5e07e2'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732', 'urn:uuid:0f88d1d2-064c-4cf7-87ca-745acb5e07e2', '0f88d1d2-064c-4cf7-87ca-745acb5e07e2');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732'
              AND id = 'urn:uuid:3857b6e1-5094-435b-924f-44af4c9eead4'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732', 'urn:uuid:3857b6e1-5094-435b-924f-44af4c9eead4', '3857b6e1-5094-435b-924f-44af4c9eead4');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:1553d4ad-273a-4593-8539-73f09d674126'
              AND id = 'urn:uuid:630b14ae-23b9-4b73-88b5-fae109633c79'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:1553d4ad-273a-4593-8539-73f09d674126', 'urn:uuid:630b14ae-23b9-4b73-88b5-fae109633c79', '630b14ae-23b9-4b73-88b5-fae109633c79');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:1553d4ad-273a-4593-8539-73f09d674126'
              AND id = 'urn:uuid:445f47db-1b53-401f-b1c0-49f40eaf6c56'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:1553d4ad-273a-4593-8539-73f09d674126', 'urn:uuid:445f47db-1b53-401f-b1c0-49f40eaf6c56', '445f47db-1b53-401f-b1c0-49f40eaf6c56');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
              AND id = 'urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', 'urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039', '028e3a8e-a71b-4743-817c-e3ec5e941039');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
              AND id = 'urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', 'urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc', '9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
              AND id = 'urn:uuid:1553d4ad-273a-4593-8539-73f09d674126'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', 'urn:uuid:1553d4ad-273a-4593-8539-73f09d674126', '1553d4ad-273a-4593-8539-73f09d674126');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
              AND id = 'urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', 'urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e', '34d5a28c-d875-4980-90b9-88a11ed53e5e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
              AND id = 'urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', 'urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca', 'f36ab678-65ab-4dd2-8b33-edb2d2745aca');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
              AND id = 'urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', 'urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e', '2495ca87-4799-4788-ab0f-ab05dc828a7e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
              AND id = 'urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', 'urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596', '9d6d4fd3-a86a-45eb-9ddf-1455eda98596');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
              AND id = 'urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', 'urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a', 'f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
              AND id = 'urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', 'urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6', 'fed26bb0-4ca5-46b1-b36e-d1d800c54af6');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
              AND id = 'urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', 'urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e', '318a53e9-0871-48f9-9e2e-77075960335e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
              AND id = 'urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', 'urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7', 'f9e9754f-191d-4b01-a53b-0b209384eff7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
              AND id = 'urn:uuid:546332ed-e620-40e1-8166-9d82422431f7'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', 'urn:uuid:546332ed-e620-40e1-8166-9d82422431f7', '546332ed-e620-40e1-8166-9d82422431f7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
              AND id = 'urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', 'urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4', '09463eab-f4e9-4b27-b0f9-b9e82fe47bf4');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
              AND id = 'urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', 'urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59', 'c97bf26e-c91e-48f1-8301-f90ec1087f59');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
              AND id = 'urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', 'urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b', 'db4f17d6-ec55-4dd9-9bf8-c82855f47e5b');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
              AND id = 'urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', 'urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732', 'fc302fe1-18ab-4f6a-90b3-60fb9e387732');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
              AND id = 'urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', 'urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b', 'e2bb4585-2df8-46e2-8cd1-3aa3b79e372b');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
              AND id = 'urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', 'urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228', '66187ac6-3eb4-4fb8-abea-dabbbcc21228');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
              AND id = 'urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', 'urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d', 'd4a8f709-aa45-475f-9602-018127b0d36d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
              AND id = 'urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', 'urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e', 'fccbd7ba-035f-4fad-b905-a8a71c3f462e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
              AND id = 'urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', 'urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d', 'e09ffbaf-183c-4089-a256-f6e23cd1462d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
              AND id = 'urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', 'urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9', '482818a8-2646-45a0-8de4-579685efd8c9');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
              AND id = 'urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', 'urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced', '60b573f5-8ae0-4e76-9577-cf946f9ceced');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
              AND id = 'urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', 'urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542', '338598c7-a1fb-4568-9e5b-7a1bc7d27542');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
              AND id = 'urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', 'urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659', '55c10558-471b-4a0c-b453-de3ff0bee659');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
              AND id = 'urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', 'urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74', 'f6e33818-e515-4882-b77e-dbb5bb5c8a74');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
              AND id = 'urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', 'urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389', '20bf29c8-e467-44ed-bbf6-d9b24637e389');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
              AND id = 'urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', 'urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3', '30870315-6a8e-4f7f-9071-cde29acf7ca3');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
              AND id = 'urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', 'urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d', 'd5a9fbff-708c-4bbe-93d8-01f16864513d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
              AND id = 'urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', 'urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d', 'd9df9966-4da5-42f9-8924-0946d0631b7d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
              AND id = 'urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', 'urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40', '0345a5e3-5c0f-43a1-89f7-b07d0255ba40');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
              AND id = 'urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', 'urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547', 'b687c70b-2e42-4aa4-9bd3-b01b6d916547');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
              AND id = 'urn:uuid:5de9e7a6-0cbd-46c9-955c-931fa3fc9438'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', 'urn:uuid:5de9e7a6-0cbd-46c9-955c-931fa3fc9438', '5de9e7a6-0cbd-46c9-955c-931fa3fc9438');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
              AND id = 'urn:uuid:6bb155bb-1e2a-4928-9b6e-ecf2d3bfc256'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', 'urn:uuid:6bb155bb-1e2a-4928-9b6e-ecf2d3bfc256', '6bb155bb-1e2a-4928-9b6e-ecf2d3bfc256');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
              AND id = 'urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', 'urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61', '1e3f162e-89db-4be8-8882-8caa26122c61');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
              AND id = 'urn:uuid:a97230c5-41aa-404d-a843-25adcbcc7a0d'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', 'urn:uuid:a97230c5-41aa-404d-a843-25adcbcc7a0d', 'a97230c5-41aa-404d-a843-25adcbcc7a0d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
              AND id = 'urn:uuid:734937a2-a242-4a8f-8dbe-a9c281e080aa'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', 'urn:uuid:734937a2-a242-4a8f-8dbe-a9c281e080aa', '734937a2-a242-4a8f-8dbe-a9c281e080aa');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
              AND id = 'urn:uuid:d71846a2-58f4-46a4-80f0-c4904b47e167'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', 'urn:uuid:d71846a2-58f4-46a4-80f0-c4904b47e167', 'd71846a2-58f4-46a4-80f0-c4904b47e167');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
              AND id = 'urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', 'urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3', 'd281057e-63a6-4d93-926b-08c1d75db7a3');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_childs
            WHERE asset_as_built_id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
              AND id = 'urn:uuid:2be97c42-4549-473d-bba9-4be8138b8ae4'
        ) THEN
            INSERT INTO assets_as_built_childs (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', 'urn:uuid:2be97c42-4549-473d-bba9-4be8138b8ae4', '2be97c42-4549-473d-bba9-4be8138b8ae4');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:412ae753-71d0-4018-bace-23b2f5cebe04'
              AND id = 'urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:412ae753-71d0-4018-bace-23b2f5cebe04', 'urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74', 'f6e33818-e515-4882-b77e-dbb5bb5c8a74');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:167847d0-9d47-4fa9-b745-17e329a74d13'
              AND id = 'urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:167847d0-9d47-4fa9-b745-17e329a74d13', 'urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74', 'f6e33818-e515-4882-b77e-dbb5bb5c8a74');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:7977a75e-5d8d-427a-810d-7c6157afac66'
              AND id = 'urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:7977a75e-5d8d-427a-810d-7c6157afac66', 'urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4', '09463eab-f4e9-4b27-b0f9-b9e82fe47bf4');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:e1e3b4ca-3c5b-4602-a0e1-d74756747975'
              AND id = 'urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:e1e3b4ca-3c5b-4602-a0e1-d74756747975', 'urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4', '09463eab-f4e9-4b27-b0f9-b9e82fe47bf4');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:45f9d032-b582-4da3-a517-a3a339a6d3de'
              AND id = 'urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:45f9d032-b582-4da3-a517-a3a339a6d3de', 'urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7', 'f9e9754f-191d-4b01-a53b-0b209384eff7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:ae399247-404f-40af-b8f4-9aeb307f6172'
              AND id = 'urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:ae399247-404f-40af-b8f4-9aeb307f6172', 'urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7', 'f9e9754f-191d-4b01-a53b-0b209384eff7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:cbc52ac9-c783-4368-ab50-09b1322e0197'
              AND id = 'urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:cbc52ac9-c783-4368-ab50-09b1322e0197', 'urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61', '1e3f162e-89db-4be8-8882-8caa26122c61');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:a8fa8f18-4956-4686-b3fb-a6f1c67940d4'
              AND id = 'urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:a8fa8f18-4956-4686-b3fb-a6f1c67940d4', 'urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61', '1e3f162e-89db-4be8-8882-8caa26122c61');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:1bbf4474-38bb-4dca-abaa-89a5ad04176f'
              AND id = 'urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:1bbf4474-38bb-4dca-abaa-89a5ad04176f', 'urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6', 'fed26bb0-4ca5-46b1-b36e-d1d800c54af6');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:9ecdd941-0012-41b4-8b2c-0189507d8dbf'
              AND id = 'urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9ecdd941-0012-41b4-8b2c-0189507d8dbf', 'urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6', 'fed26bb0-4ca5-46b1-b36e-d1d800c54af6');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:f1c427c4-48fe-4801-a8f7-8cecb22bf5ac'
              AND id = 'urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f1c427c4-48fe-4801-a8f7-8cecb22bf5ac', 'urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547', 'b687c70b-2e42-4aa4-9bd3-b01b6d916547');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:8b7254a2-e31c-416e-8d0d-03e5d8524c6b'
              AND id = 'urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:8b7254a2-e31c-416e-8d0d-03e5d8524c6b', 'urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547', 'b687c70b-2e42-4aa4-9bd3-b01b6d916547');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:dd8f0128-0054-4aa4-ba55-72addbbe7451'
              AND id = 'urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:dd8f0128-0054-4aa4-ba55-72addbbe7451', 'urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039', '028e3a8e-a71b-4743-817c-e3ec5e941039');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:118bf045-2598-499c-92f0-e3204827ca20'
              AND id = 'urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:118bf045-2598-499c-92f0-e3204827ca20', 'urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039', '028e3a8e-a71b-4743-817c-e3ec5e941039');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:b9afbdd8-5a1b-4688-9b43-0a8f12c1053a'
              AND id = 'urn:uuid:546332ed-e620-40e1-8166-9d82422431f7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:b9afbdd8-5a1b-4688-9b43-0a8f12c1053a', 'urn:uuid:546332ed-e620-40e1-8166-9d82422431f7', '546332ed-e620-40e1-8166-9d82422431f7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:558a63f2-0ab5-4917-88b9-6e9c646ab3ba'
              AND id = 'urn:uuid:546332ed-e620-40e1-8166-9d82422431f7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:558a63f2-0ab5-4917-88b9-6e9c646ab3ba', 'urn:uuid:546332ed-e620-40e1-8166-9d82422431f7', '546332ed-e620-40e1-8166-9d82422431f7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:9ce0e54e-e069-4c3c-bae3-6451b1bb1d24'
              AND id = 'urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9ce0e54e-e069-4c3c-bae3-6451b1bb1d24', 'urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e', '34d5a28c-d875-4980-90b9-88a11ed53e5e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:bf7581f7-eb6c-442b-93f8-3dbfe9f16407'
              AND id = 'urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:bf7581f7-eb6c-442b-93f8-3dbfe9f16407', 'urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e', '34d5a28c-d875-4980-90b9-88a11ed53e5e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:110a51cf-24d9-452e-8737-5465a7596a7c'
              AND id = 'urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:110a51cf-24d9-452e-8737-5465a7596a7c', 'urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e', '318a53e9-0871-48f9-9e2e-77075960335e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:bb5d6cb0-8ee2-43c0-958a-f011444ad085'
              AND id = 'urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:bb5d6cb0-8ee2-43c0-958a-f011444ad085', 'urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e', '318a53e9-0871-48f9-9e2e-77075960335e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:8aeb0568-b226-48c1-a182-91d7d2fce440'
              AND id = 'urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:8aeb0568-b226-48c1-a182-91d7d2fce440', 'urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659', '55c10558-471b-4a0c-b453-de3ff0bee659');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:caaace62-9b9d-4791-ac8a-283fc84ce393'
              AND id = 'urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:caaace62-9b9d-4791-ac8a-283fc84ce393', 'urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659', '55c10558-471b-4a0c-b453-de3ff0bee659');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:1131e656-db10-487a-a689-df00700836c3'
              AND id = 'urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:1131e656-db10-487a-a689-df00700836c3', 'urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e', '2495ca87-4799-4788-ab0f-ab05dc828a7e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:e0502a8d-edac-4df8-ad25-720262175157'
              AND id = 'urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:e0502a8d-edac-4df8-ad25-720262175157', 'urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e', '2495ca87-4799-4788-ab0f-ab05dc828a7e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:7a31a848-d224-4606-9e22-081352fea6da'
              AND id = 'urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:7a31a848-d224-4606-9e22-081352fea6da', 'urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc', '9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:77ea2d2e-e13f-4e4e-bc06-884c649a5c29'
              AND id = 'urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:77ea2d2e-e13f-4e4e-bc06-884c649a5c29', 'urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc', '9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:847a7145-0ee7-4b68-88f2-f96a40437bc4'
              AND id = 'urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:847a7145-0ee7-4b68-88f2-f96a40437bc4', 'urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca', 'f36ab678-65ab-4dd2-8b33-edb2d2745aca');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:aaf37dd0-eb97-4b84-b6f4-3824edaba336'
              AND id = 'urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:aaf37dd0-eb97-4b84-b6f4-3824edaba336', 'urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca', 'f36ab678-65ab-4dd2-8b33-edb2d2745aca');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:8d9b19df-8ac1-4836-8f9d-fd554d044c2d'
              AND id = 'urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:8d9b19df-8ac1-4836-8f9d-fd554d044c2d', 'urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596', '9d6d4fd3-a86a-45eb-9ddf-1455eda98596');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:8b14245d-5790-444f-bdcc-601a844ee4fd'
              AND id = 'urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:8b14245d-5790-444f-bdcc-601a844ee4fd', 'urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596', '9d6d4fd3-a86a-45eb-9ddf-1455eda98596');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:964aa964-2006-42a6-a2a2-568bfd396204'
              AND id = 'urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:964aa964-2006-42a6-a2a2-568bfd396204', 'urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389', '20bf29c8-e467-44ed-bbf6-d9b24637e389');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:770dc5fa-7649-4d2b-952a-718b65883bcd'
              AND id = 'urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:770dc5fa-7649-4d2b-952a-718b65883bcd', 'urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389', '20bf29c8-e467-44ed-bbf6-d9b24637e389');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:be1eabe9-89d5-4406-b443-41937127934c'
              AND id = 'urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:be1eabe9-89d5-4406-b443-41937127934c', 'urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3', 'd281057e-63a6-4d93-926b-08c1d75db7a3');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:d50dbcdd-6cb1-4829-8ec5-ac8f2ee7ab31'
              AND id = 'urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d50dbcdd-6cb1-4829-8ec5-ac8f2ee7ab31', 'urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3', 'd281057e-63a6-4d93-926b-08c1d75db7a3');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:9abd269a-fbd0-4a6a-aa7a-21edb06a5451'
              AND id = 'urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9abd269a-fbd0-4a6a-aa7a-21edb06a5451', 'urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced', '60b573f5-8ae0-4e76-9577-cf946f9ceced');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:a25f5643-d6ea-436d-b3f6-1e236acbfdbe'
              AND id = 'urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:a25f5643-d6ea-436d-b3f6-1e236acbfdbe', 'urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced', '60b573f5-8ae0-4e76-9577-cf946f9ceced');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:5da813a2-ab39-41a1-b29c-af9c9bca58b5'
              AND id = 'urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:5da813a2-ab39-41a1-b29c-af9c9bca58b5', 'urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3', '30870315-6a8e-4f7f-9071-cde29acf7ca3');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:20469b3e-84f3-41dd-9cfc-331ad57f1af4'
              AND id = 'urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:20469b3e-84f3-41dd-9cfc-331ad57f1af4', 'urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3', '30870315-6a8e-4f7f-9071-cde29acf7ca3');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:43660301-90c5-406f-a16e-95bc24131db8'
              AND id = 'urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:43660301-90c5-406f-a16e-95bc24131db8', 'urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228', '66187ac6-3eb4-4fb8-abea-dabbbcc21228');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:af8ce1fb-0c2c-4b80-bd20-7b2bba6bd8ee'
              AND id = 'urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:af8ce1fb-0c2c-4b80-bd20-7b2bba6bd8ee', 'urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228', '66187ac6-3eb4-4fb8-abea-dabbbcc21228');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:3a86b5ce-9520-42f5-bf65-24a1f8640772'
              AND id = 'urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:3a86b5ce-9520-42f5-bf65-24a1f8640772', 'urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d', 'e09ffbaf-183c-4089-a256-f6e23cd1462d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:f6a1fbe1-7820-4e67-92c7-803c3ca3105e'
              AND id = 'urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f6a1fbe1-7820-4e67-92c7-803c3ca3105e', 'urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d', 'e09ffbaf-183c-4089-a256-f6e23cd1462d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:9c1a3264-7ad7-4356-805c-e233f882d14e'
              AND id = 'urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9c1a3264-7ad7-4356-805c-e233f882d14e', 'urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a', 'f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:86a0031c-c349-4b0e-8681-cd7c1e820b7c'
              AND id = 'urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:86a0031c-c349-4b0e-8681-cd7c1e820b7c', 'urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a', 'f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:18533875-a398-456b-a4bd-29884f97dee8'
              AND id = 'urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:18533875-a398-456b-a4bd-29884f97dee8', 'urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542', '338598c7-a1fb-4568-9e5b-7a1bc7d27542');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:c82f5ddd-a237-4c79-bfba-62b63ac4f9f3'
              AND id = 'urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:c82f5ddd-a237-4c79-bfba-62b63ac4f9f3', 'urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542', '338598c7-a1fb-4568-9e5b-7a1bc7d27542');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:e9a88bef-27ec-4353-804a-08c4cabe2d23'
              AND id = 'urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:e9a88bef-27ec-4353-804a-08c4cabe2d23', 'urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b', 'db4f17d6-ec55-4dd9-9bf8-c82855f47e5b');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:f0b3dbda-4ffb-4fc2-9b97-c939043de195'
              AND id = 'urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f0b3dbda-4ffb-4fc2-9b97-c939043de195', 'urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b', 'db4f17d6-ec55-4dd9-9bf8-c82855f47e5b');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:c32b38d7-60eb-469c-a553-3d25f5e3333c'
              AND id = 'urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:c32b38d7-60eb-469c-a553-3d25f5e3333c', 'urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b', 'e2bb4585-2df8-46e2-8cd1-3aa3b79e372b');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:baa271b7-b185-4cae-8b43-358c78edf786'
              AND id = 'urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:baa271b7-b185-4cae-8b43-358c78edf786', 'urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b', 'e2bb4585-2df8-46e2-8cd1-3aa3b79e372b');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:3463735b-5d4c-4832-a87f-7161448d8063'
              AND id = 'urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:3463735b-5d4c-4832-a87f-7161448d8063', 'urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59', 'c97bf26e-c91e-48f1-8301-f90ec1087f59');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:9751cc2b-d2b1-421d-9c1a-f8acc37540fd'
              AND id = 'urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9751cc2b-d2b1-421d-9c1a-f8acc37540fd', 'urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59', 'c97bf26e-c91e-48f1-8301-f90ec1087f59');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:24812aa1-8c41-4020-bf8f-796f297d8078'
              AND id = 'urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:24812aa1-8c41-4020-bf8f-796f297d8078', 'urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9', '482818a8-2646-45a0-8de4-579685efd8c9');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:45db6770-3a6e-413f-8644-76b7af8f1172'
              AND id = 'urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:45db6770-3a6e-413f-8644-76b7af8f1172', 'urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9', '482818a8-2646-45a0-8de4-579685efd8c9');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:ecc7422e-9546-475f-abb2-a1513224f8aa'
              AND id = 'urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:ecc7422e-9546-475f-abb2-a1513224f8aa', 'urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40', '0345a5e3-5c0f-43a1-89f7-b07d0255ba40');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:62336610-d29b-40a4-9d59-83271bce9283'
              AND id = 'urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:62336610-d29b-40a4-9d59-83271bce9283', 'urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40', '0345a5e3-5c0f-43a1-89f7-b07d0255ba40');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:597e93b5-b1ff-404a-ba51-5acc2042970e'
              AND id = 'urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:597e93b5-b1ff-404a-ba51-5acc2042970e', 'urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d', 'd9df9966-4da5-42f9-8924-0946d0631b7d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:a3d25313-c35f-400f-aac0-1802440bcd8a'
              AND id = 'urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:a3d25313-c35f-400f-aac0-1802440bcd8a', 'urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d', 'd9df9966-4da5-42f9-8924-0946d0631b7d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:e86e73a3-9c76-4c72-a9ae-bbc384907e4f'
              AND id = 'urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:e86e73a3-9c76-4c72-a9ae-bbc384907e4f', 'urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e', 'fccbd7ba-035f-4fad-b905-a8a71c3f462e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:3d2ceec7-ae45-4f48-a657-2186bbf0382a'
              AND id = 'urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:3d2ceec7-ae45-4f48-a657-2186bbf0382a', 'urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e', 'fccbd7ba-035f-4fad-b905-a8a71c3f462e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:059ec4a4-be81-41c4-b898-131d8edaa62d'
              AND id = 'urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:059ec4a4-be81-41c4-b898-131d8edaa62d', 'urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d', 'd4a8f709-aa45-475f-9602-018127b0d36d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:0dfe5b22-79b7-4e3e-b8ad-6f51e092ffd7'
              AND id = 'urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:0dfe5b22-79b7-4e3e-b8ad-6f51e092ffd7', 'urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d', 'd4a8f709-aa45-475f-9602-018127b0d36d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:5a67ac04-beb8-497a-862c-0bfa8b76d5fe'
              AND id = 'urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:5a67ac04-beb8-497a-862c-0bfa8b76d5fe', 'urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d', 'd5a9fbff-708c-4bbe-93d8-01f16864513d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:6cb80544-9740-46ef-b196-d06d1988153a'
              AND id = 'urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:6cb80544-9740-46ef-b196-d06d1988153a', 'urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d', 'd5a9fbff-708c-4bbe-93d8-01f16864513d');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:0f88d1d2-064c-4cf7-87ca-745acb5e07e2'
              AND id = 'urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:0f88d1d2-064c-4cf7-87ca-745acb5e07e2', 'urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732', 'fc302fe1-18ab-4f6a-90b3-60fb9e387732');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:3857b6e1-5094-435b-924f-44af4c9eead4'
              AND id = 'urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:3857b6e1-5094-435b-924f-44af4c9eead4', 'urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732', 'fc302fe1-18ab-4f6a-90b3-60fb9e387732');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:630b14ae-23b9-4b73-88b5-fae109633c79'
              AND id = 'urn:uuid:1553d4ad-273a-4593-8539-73f09d674126'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:630b14ae-23b9-4b73-88b5-fae109633c79', 'urn:uuid:1553d4ad-273a-4593-8539-73f09d674126', '1553d4ad-273a-4593-8539-73f09d674126');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:445f47db-1b53-401f-b1c0-49f40eaf6c56'
              AND id = 'urn:uuid:1553d4ad-273a-4593-8539-73f09d674126'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:445f47db-1b53-401f-b1c0-49f40eaf6c56', 'urn:uuid:1553d4ad-273a-4593-8539-73f09d674126', '1553d4ad-273a-4593-8539-73f09d674126');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039'
              AND id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:028e3a8e-a71b-4743-817c-e3ec5e941039', 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', '9a8ab85d-58be-4045-8caa-8fab8fe511e6');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc'
              AND id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9e3e8378-0e0d-4ca8-a99f-2fdf8da66acc', 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', '9a8ab85d-58be-4045-8caa-8fab8fe511e6');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:1553d4ad-273a-4593-8539-73f09d674126'
              AND id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:1553d4ad-273a-4593-8539-73f09d674126', 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', '9a8ab85d-58be-4045-8caa-8fab8fe511e6');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e'
              AND id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:34d5a28c-d875-4980-90b9-88a11ed53e5e', 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', '9a8ab85d-58be-4045-8caa-8fab8fe511e6');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca'
              AND id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f36ab678-65ab-4dd2-8b33-edb2d2745aca', 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', '9a8ab85d-58be-4045-8caa-8fab8fe511e6');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e'
              AND id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:2495ca87-4799-4788-ab0f-ab05dc828a7e', 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', '9a8ab85d-58be-4045-8caa-8fab8fe511e6');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596'
              AND id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:9d6d4fd3-a86a-45eb-9ddf-1455eda98596', 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', '9a8ab85d-58be-4045-8caa-8fab8fe511e6');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a'
              AND id = 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f8a51fca-a5e4-4e13-8b9f-e2a6ad330d1a', 'urn:uuid:9a8ab85d-58be-4045-8caa-8fab8fe511e6', '9a8ab85d-58be-4045-8caa-8fab8fe511e6');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6'
              AND id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:fed26bb0-4ca5-46b1-b36e-d1d800c54af6', 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', '4d2da80a-f483-4c2b-8207-8d794079dee7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e'
              AND id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:318a53e9-0871-48f9-9e2e-77075960335e', 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', '4d2da80a-f483-4c2b-8207-8d794079dee7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7'
              AND id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f9e9754f-191d-4b01-a53b-0b209384eff7', 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', '4d2da80a-f483-4c2b-8207-8d794079dee7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:546332ed-e620-40e1-8166-9d82422431f7'
              AND id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:546332ed-e620-40e1-8166-9d82422431f7', 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', '4d2da80a-f483-4c2b-8207-8d794079dee7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4'
              AND id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:09463eab-f4e9-4b27-b0f9-b9e82fe47bf4', 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', '4d2da80a-f483-4c2b-8207-8d794079dee7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59'
              AND id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:c97bf26e-c91e-48f1-8301-f90ec1087f59', 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', '4d2da80a-f483-4c2b-8207-8d794079dee7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b'
              AND id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:db4f17d6-ec55-4dd9-9bf8-c82855f47e5b', 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', '4d2da80a-f483-4c2b-8207-8d794079dee7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732'
              AND id = 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:fc302fe1-18ab-4f6a-90b3-60fb9e387732', 'urn:uuid:4d2da80a-f483-4c2b-8207-8d794079dee7', '4d2da80a-f483-4c2b-8207-8d794079dee7');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b'
              AND id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:e2bb4585-2df8-46e2-8cd1-3aa3b79e372b', 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', '78673ce4-9037-4f34-9014-9797080e768e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228'
              AND id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:66187ac6-3eb4-4fb8-abea-dabbbcc21228', 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', '78673ce4-9037-4f34-9014-9797080e768e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d'
              AND id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d4a8f709-aa45-475f-9602-018127b0d36d', 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', '78673ce4-9037-4f34-9014-9797080e768e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e'
              AND id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:fccbd7ba-035f-4fad-b905-a8a71c3f462e', 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', '78673ce4-9037-4f34-9014-9797080e768e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d'
              AND id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:e09ffbaf-183c-4089-a256-f6e23cd1462d', 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', '78673ce4-9037-4f34-9014-9797080e768e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9'
              AND id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:482818a8-2646-45a0-8de4-579685efd8c9', 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', '78673ce4-9037-4f34-9014-9797080e768e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced'
              AND id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:60b573f5-8ae0-4e76-9577-cf946f9ceced', 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', '78673ce4-9037-4f34-9014-9797080e768e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542'
              AND id = 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:338598c7-a1fb-4568-9e5b-7a1bc7d27542', 'urn:uuid:78673ce4-9037-4f34-9014-9797080e768e', '78673ce4-9037-4f34-9014-9797080e768e');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659'
              AND id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:55c10558-471b-4a0c-b453-de3ff0bee659', 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', '393f1ebb-49d4-4fe8-8ffc-7d85baa2f068');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74'
              AND id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:f6e33818-e515-4882-b77e-dbb5bb5c8a74', 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', '393f1ebb-49d4-4fe8-8ffc-7d85baa2f068');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389'
              AND id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:20bf29c8-e467-44ed-bbf6-d9b24637e389', 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', '393f1ebb-49d4-4fe8-8ffc-7d85baa2f068');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3'
              AND id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:30870315-6a8e-4f7f-9071-cde29acf7ca3', 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', '393f1ebb-49d4-4fe8-8ffc-7d85baa2f068');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d'
              AND id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d5a9fbff-708c-4bbe-93d8-01f16864513d', 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', '393f1ebb-49d4-4fe8-8ffc-7d85baa2f068');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d'
              AND id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d9df9966-4da5-42f9-8924-0946d0631b7d', 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', '393f1ebb-49d4-4fe8-8ffc-7d85baa2f068');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40'
              AND id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:0345a5e3-5c0f-43a1-89f7-b07d0255ba40', 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', '393f1ebb-49d4-4fe8-8ffc-7d85baa2f068');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547'
              AND id = 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:b687c70b-2e42-4aa4-9bd3-b01b6d916547', 'urn:uuid:393f1ebb-49d4-4fe8-8ffc-7d85baa2f068', '393f1ebb-49d4-4fe8-8ffc-7d85baa2f068');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:5de9e7a6-0cbd-46c9-955c-931fa3fc9438'
              AND id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:5de9e7a6-0cbd-46c9-955c-931fa3fc9438', 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', '53f3371e-81e2-4bea-953e-d683688550cc');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:6bb155bb-1e2a-4928-9b6e-ecf2d3bfc256'
              AND id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:6bb155bb-1e2a-4928-9b6e-ecf2d3bfc256', 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', '53f3371e-81e2-4bea-953e-d683688550cc');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61'
              AND id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:1e3f162e-89db-4be8-8882-8caa26122c61', 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', '53f3371e-81e2-4bea-953e-d683688550cc');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:a97230c5-41aa-404d-a843-25adcbcc7a0d'
              AND id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:a97230c5-41aa-404d-a843-25adcbcc7a0d', 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', '53f3371e-81e2-4bea-953e-d683688550cc');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:734937a2-a242-4a8f-8dbe-a9c281e080aa'
              AND id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:734937a2-a242-4a8f-8dbe-a9c281e080aa', 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', '53f3371e-81e2-4bea-953e-d683688550cc');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:d71846a2-58f4-46a4-80f0-c4904b47e167'
              AND id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d71846a2-58f4-46a4-80f0-c4904b47e167', 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', '53f3371e-81e2-4bea-953e-d683688550cc');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3'
              AND id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:d281057e-63a6-4d93-926b-08c1d75db7a3', 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', '53f3371e-81e2-4bea-953e-d683688550cc');
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM assets_as_built_parents
            WHERE asset_as_built_id = 'urn:uuid:2be97c42-4549-473d-bba9-4be8138b8ae4'
              AND id = 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc'
        ) THEN
            INSERT INTO assets_as_built_parents (asset_as_built_id, id, id_short)
            VALUES ('urn:uuid:2be97c42-4549-473d-bba9-4be8138b8ae4', 'urn:uuid:53f3371e-81e2-4bea-953e-d683688550cc', '53f3371e-81e2-4bea-953e-d683688550cc');
        END IF;

        END IF;
    END
$$;
