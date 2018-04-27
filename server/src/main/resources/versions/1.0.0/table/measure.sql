drop table if exists measure;
create table if not exists measure (
	measure_id serial primary key,
	measure_name varchar(100),
	measure_json text,
	last_updated timestamp default current_timestamp
);

create trigger tr_update_measure_last_updated
before update on measure
for each row
execute procedure fn_update_timestamp();