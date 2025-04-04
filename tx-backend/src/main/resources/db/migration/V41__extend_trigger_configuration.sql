ALTER TABLE trigger_configuration
    ADD COLUMN cron_expression_aas_cleanup_ttl_reached text;

ALTER TABLE trigger_configuration
    ADD COLUMN cron_expression_aas_lookup_ttl_reached text;
