ALTER TABLE assets_as_planned
    ADD COLUMN "tombstone" varchar NULL;
ALTER TABLE assets_as_built
    ADD COLUMN "tombstone" varchar NULL;
