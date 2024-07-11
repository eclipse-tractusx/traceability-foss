CREATE VIEW contract_agreement_full AS
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
UNION ALL
SELECT
    id,
    contract_agreement_id,
    global_asset_id,
    type,
    created,
    updated,
    'contract_agreement_as_planned' AS source_table
FROM
    contract_agreement_as_planned;
