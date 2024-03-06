ALTER TABLE assets_as_planned
    ADD COLUMN "contract_agreement_id" varchar(255) NULL;
ALTER TABLE assets_as_built
    ADD COLUMN "contract_agreement_id" varchar(255) NULL;

ALTER TABLE assets_as_planned
    ADD COLUMN "created" timestamptz NULL DEFAULT now();
ALTER TABLE assets_as_built
    ADD COLUMN "created" timestamptz NULL DEFAULT now();
