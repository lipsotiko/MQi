drop table if exists t_measure;
create table if not exists t_measure (
	measure_id serial primary key,
	file_name varchar(100),
	file_bytes bytea,
	last_updated timestamp default current_timestamp
);

create trigger tr_update_t_measure_last_updated
before update on t_measure 
for each row
execute procedure fn_update_timestamp();