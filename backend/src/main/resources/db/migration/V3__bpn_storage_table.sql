create table bpn_storage
(
	manufacturer_id   varchar(255) not null,
	manufacturer_name varchar(255) not null,
	primary key (manufacturer_id)
);

insert into bpn_storage values('BPNL00000000BJTL', 'Sub Tier C');
insert into bpn_storage values('BPNL00000003AXS3', 'Sub Tier B');
insert into bpn_storage values('BPNL00000003AYRE', 'OEM A');
insert into bpn_storage values('BPNL00000003AZQP', 'OEM C');
insert into bpn_storage values('BPNL00000003B0Q0', 'N-Tier A');
insert into bpn_storage values('BPNL00000003B2OM', 'Tier A');
insert into bpn_storage values('BPNL00000003B3NX', 'Sub Tier A');
insert into bpn_storage values('BPNL00000003B5MJ', 'Tier B');
insert into bpn_storage values('BPNL00000003CSGV', 'Tier C');
