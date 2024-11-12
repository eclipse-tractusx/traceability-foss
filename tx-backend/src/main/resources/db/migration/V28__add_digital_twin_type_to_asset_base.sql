ALTER TABLE assets_as_built
    ADD COLUMN digital_twin_type varchar;

ALTER TABLE assets_as_planned
    ADD COLUMN digital_twin_type varchar;
