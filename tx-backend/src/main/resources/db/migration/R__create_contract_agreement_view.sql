
ALTER VIEW contract_agreement_view RENAME COLUMN asset_type TO type;

CREATE OR REPLACE VIEW contract_agreement_view (id, contract_agreement_id, type, created) AS
SELECT *
FROM (
         (SELECT assets_as_built.id,
                 contract_agreement_id,
                 'ASSET_AS_BUILT' AS type,
                 created
          FROM assets_as_built
          WHERE contract_agreement_id IS NOT NULL)
         UNION ALL
         (SELECT assets_as_planned.id,
                 contract_agreement_id,
                 'ASSET_AS_PLANNED' AS type,
                 created
          FROM assets_as_planned
          WHERE contract_agreement_id IS NOT NULL)
     ) results
ORDER BY created DESC;
