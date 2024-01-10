ALTER TABLE assets_as_planned ADD COLUMN "import_state" varchar(25) NOT NULL;
ALTER TABLE assets_as_built ADD COLUMN "import_state" varchar(25) NOT NULL;
ALTER TABLE assets_as_planned ADD COLUMN "import_note" varchar(100);
ALTER TABLE assets_as_built ADD COLUMN "import_note" varchar(100);
