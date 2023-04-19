alter table if exists investigation
	add column side int4;

alter table if exists investigation
	add column accept_reason varchar(255);

alter table if exists investigation
	add column decline_reason varchar(255);
