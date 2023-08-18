DROP TABLE shell_descriptor;

CREATE TABLE shell_descriptor
(
    id              SERIAL PRIMARY KEY,
    created         TIMESTAMPTZ NOT NULL,
    updated         TIMESTAMPTZ NOT NULL,
    global_asset_id TEXT NOT NULL UNIQUE
);
