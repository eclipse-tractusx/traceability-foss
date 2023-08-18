create table bpn_edc_mappings
(
	bpn varchar(255),
	url varchar(255),
    created timestamptz,
    updated timestamptz,
	primary key (bpn),
	unique(url)
);

insert into bpn_edc_mappings values('BPNL00000003AYRE', 'https://tracex-consumer-controlplane.dev.demo.catena-x.net', current_timestamp, null);
insert into bpn_edc_mappings values('BPNL00000003B2OM', 'https://tracex-test-consumer-controlplane.dev.demo.catena-x.net', current_timestamp, null);
