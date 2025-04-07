ALTER TABLE trigger_configuration
    ADD COLUMN aas_limit integer;

UPDATE public.trigger_configuration
SET cron_expression_aas_cleanup_ttl_reached = '0 1 * * *',
    cron_expression_aas_lookup_ttl_reached  = '0 3 * * *',
    aas_limit                               = 1000
WHERE id = 1;
