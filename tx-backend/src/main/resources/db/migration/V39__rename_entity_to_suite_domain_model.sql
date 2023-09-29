ALTER TABLE alert_notification
    RENAME COLUMN sender_bpn_number TO created_by;
ALTER TABLE alert_notification
    RENAME COLUMN sender_manufacturer_name TO created_by_name;
ALTER TABLE alert_notification
    RENAME COLUMN receiver_bpn_number TO send_to;
ALTER TABLE alert_notification
    RENAME COLUMN receiver_manufacturer_name TO send_to_name;

ALTER TABLE investigation_notification
    RENAME COLUMN sender_bpn_number TO created_by;
ALTER TABLE investigation_notification
    RENAME COLUMN sender_manufacturer_name TO created_by_name;
ALTER TABLE investigation_notification
    RENAME COLUMN receiver_bpn_number TO send_to;
ALTER TABLE investigation_notification
    RENAME COLUMN receiver_manufacturer_name TO send_to_name;



