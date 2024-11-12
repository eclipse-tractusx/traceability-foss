UPDATE public.assets_as_built
SET digital_twin_type =
        CASE
            WHEN semantic_data_model = 'SERIALPART'
                OR semantic_data_model = 'JUSTINSEQUENCE'
                OR semantic_data_model = 'BATCH' THEN 'partInstance'
            END
WHERE digital_twin_type IS NULL;

UPDATE public.assets_as_planned
SET digital_twin_type =
        CASE
            WHEN semantic_data_model = 'PARTASPLANNED'
                THEN 'partType'
            END
WHERE digital_twin_type IS NULL;
