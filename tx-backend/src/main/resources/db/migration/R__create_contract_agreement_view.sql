create or replace view contract_agreement_view as
SELECT *
FROM ((SELECT assets_as_built.id, contract_agreement_id, 'ASSET_AS_BUILT' as type, created
       FROM assets_as_built
       where contract_agreement_id is not null)
      UNION ALL
      (SELECT assets_as_planned.id, contract_agreement_id, 'ASSET_AS_PLANNED' as type, created
       FROM assets_as_planned
       where contract_agreement_id is not null)) results
ORDER BY created DESC;

