drop table if exists measure;
create table if not exists measure (
	measure_id serial primary key,
	measure_name varchar(100) not null,
	measure_json text,
	last_updated timestamp not null
);

create unique index ux_measure on measure (LOWER(measure_name));
