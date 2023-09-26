DROP TABLE bpn_edc_mappings;
DROP TABLE bpn_storage;
CREATE TABLE bpn_storage
(
	manufacturer_id   VARCHAR(255) NOT NULL,
	manufacturer_name VARCHAR(255),
    url VARCHAR(255),
    created timestamptz,
    updated timestamptz,
    PRIMARY KEY(manufacturer_id),
    UNIQUE(url)
);
