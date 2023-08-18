delete
FROM public.bpn_edc_mappings;
insert into bpn_edc_mappings
values ('BPNL00000003CML1', 'https://tracex-consumer-controlplane.dev.demo.catena-x.net', current_timestamp, null);
insert into bpn_edc_mappings
values ('BPNL00000003CNKC', 'https://tracex-test-consumer-controlplane.dev.demo.catena-x.net', current_timestamp, null);
