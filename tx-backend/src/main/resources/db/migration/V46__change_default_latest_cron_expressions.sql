UPDATE public.trigger_configuration
SET cron_expression_aas_cleanup_ttl_reached = null,
    cron_expression_aas_lookup_ttl_reached = null,
    cron_expression_map_completed_orders = null,
    cron_expression_publish_assets = null,
    cron_expression_register_order_ttl_reached = null
WHERE id = (SELECT last_value FROM public.trigger_configuration_id_seq);
