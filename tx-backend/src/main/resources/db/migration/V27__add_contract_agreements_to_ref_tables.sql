ALTER TABLE contract_agreement_as_built
    ADD COLUMN global_asset_id varchar;

ALTER TABLE contract_agreement_as_planned
    ADD COLUMN global_asset_id varchar;

ALTER TABLE contract_agreement_notification
    ADD COLUMN global_asset_id varchar;
