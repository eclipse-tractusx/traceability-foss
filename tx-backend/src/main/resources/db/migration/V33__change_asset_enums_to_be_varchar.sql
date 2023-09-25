ALTER TABLE assets_as_built
    ALTER COLUMN owner SET DATA TYPE VARCHAR(50);
ALTER TABLE assets_as_built
    ALTER COLUMN quality_type SET DATA TYPE VARCHAR(50);
ALTER TABLE assets_as_built
    ALTER COLUMN semantic_data_model SET DATA TYPE VARCHAR(50);
ALTER TABLE assets_as_planned
    ALTER COLUMN owner SET DATA TYPE VARCHAR(50);
ALTER TABLE assets_as_planned
    ALTER COLUMN quality_type SET DATA TYPE VARCHAR(50);
ALTER TABLE assets_as_planned
    ALTER COLUMN semantic_data_model SET DATA TYPE VARCHAR(50);
