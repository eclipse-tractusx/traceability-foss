-- Drop the view if it exists
DROP VIEW IF EXISTS contract_agreement_view;

-- Create the table
CREATE TABLE contract_agreement (
                                    id varchar(255) PRIMARY KEY,
                                    contract_agreement_id varchar(255),
                                    type VARCHAR(255),
                                    created TIMESTAMP
);
