ALTER TABLE contract_agreement RENAME TO contract_agreement_as_built;

ALTER TABLE contract_agreement_as_built
    ADD COLUMN updated TIMESTAMP;

CREATE TABLE IF NOT EXISTS contract_agreement_notification (
                                                  id VARCHAR(255) PRIMARY KEY,
                                                  contract_agreement_id VARCHAR(255),
                                                  created TIMESTAMP,
                                                  type VARCHAR(255),
                                                  updated TIMESTAMP);


CREATE TABLE IF NOT EXISTS contract_agreement_as_planned (
                                                               id VARCHAR(255) PRIMARY KEY,
                                                               contract_agreement_id VARCHAR(255),
                                                               created TIMESTAMP,
                                                               type VARCHAR(255),
                                                               updated TIMESTAMP);



