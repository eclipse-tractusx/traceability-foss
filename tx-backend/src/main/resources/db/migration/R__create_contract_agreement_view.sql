CREATE OR REPLACE VIEW contract_agreement_full AS
SELECT
    id,
    contract_agreement_id,
    global_asset_id,
    type,
    created,
    updated,
    'contract_agreement_as_built' AS source_table
FROM
    contract_agreement_as_built
WHERE contract_agreement_id is not null
UNION ALL
SELECT
    id,
    contract_agreement_id,
    global_asset_id,
    type,
    created,
    updated,
    'contract_agreement_notification' AS source_table
FROM
    contract_agreement_notification
WHERE contract_agreement_id is not null
UNION ALL
SELECT
    id,
    contract_agreement_id,
    global_asset_id,
    type,
    created,
    updated,
    'contract_agreement_as_planned' AS source_table
FROM contract_agreement_as_planned
WHERE contract_agreement_id is not null;
