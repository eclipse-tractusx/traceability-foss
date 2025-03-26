CREATE TABLE aas
(
    aas_id            VARCHAR(255) NOT NULL PRIMARY KEY,
    ttl               INT          NOT NULL,
    created           TIMESTAMP    NOT NULL,
    updated           TIMESTAMP    NOT NULL,
    expiration        TIMESTAMP    NOT NULL,
    actor             VARCHAR(255) NOT NULL,
    digital_twin_type VARCHAR(255) NOT NULL,
    bpn               VARCHAR(255) NOT NULL,
    global_asset_id   VARCHAR(255),

    -- Foreign key references
    CONSTRAINT fk_asset_as_built FOREIGN KEY (global_asset_id)
        REFERENCES assets_as_built (id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    CONSTRAINT fk_asset_as_planned FOREIGN KEY (global_asset_id)
        REFERENCES assets_as_planned (id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Indexes for improved query performance
CREATE INDEX idx_aas_id ON aas (aas_id); -- Fast lookup for aasId
CREATE INDEX idx_aas_expiration ON aas (expiration); -- Efficient expiry check
CREATE INDEX idx_aas_digital_twin_type ON aas (digital_twin_type); -- Useful for filtering
CREATE INDEX idx_aas_bpn ON aas (bpn); -- Useful for filtering by BPN
CREATE INDEX idx_aas_updated ON aas (updated); -- Sorting or filtering by updated timestamps
