ALTER TABLE assets_as_planned
    ADD COLUMN "created" timestamptz NULL DEFAULT now();
ALTER TABLE assets_as_built
    ADD COLUMN "created" timestamptz NULL DEFAULT now();
