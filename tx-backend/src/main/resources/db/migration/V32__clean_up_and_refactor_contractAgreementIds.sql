DELETE
from contract_agreement_as_built
WHERE contract_agreement_id is null;
DELETE
from contract_agreement_as_planned
WHERE contract_agreement_id is null;
DELETE
from contract_agreement_notification
WHERE contract_agreement_id is null;


ALTER TABLE contract_agreement_as_built
    ALTER COLUMN contract_agreement_id SET NOT NULL;
ALTER TABLE contract_agreement_as_planned
    ALTER COLUMN contract_agreement_id SET NOT NULL;
ALTER TABLE contract_agreement_notification
    ALTER COLUMN contract_agreement_id SET NOT NULL;

