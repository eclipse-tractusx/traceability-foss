ALTER TABLE notification
    DROP COLUMN IF EXISTS accept_reason,
    DROP COLUMN IF EXISTS decline_reason,
    DROP COLUMN IF EXISTS close_reason,
    ADD COLUMN target_date timestamp NULL,
    ADD COLUMN severity varchar(255) NULL;

ALTER TABLE notification_message
    DROP COLUMN IF EXISTS target_date,
    DROP COLUMN IF EXISTS severity,
    ADD COLUMN message varchar(1000) NULL;
