drop table if exists rule_param;
create table if not exists rule_param (
	rule_param_id serial primary key,
	rule_name varchar(255),
	param_name varchar(50),
	param_type varchar(50),
	display_order integer default 0
);