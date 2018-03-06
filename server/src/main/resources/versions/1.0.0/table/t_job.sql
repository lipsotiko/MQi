drop table if exists t_job;
create table if not exists t_job (
	job_id serial primary key,
	job_name varchar(255),
	process_type varchar(25) not null,
	order_id integer default 0,
	status varchar(25) default 'idle',
	start_time timestamp,
	end_time timestamp,
	last_updated timestamp default current_timestamp
);

create unique index ux_t_job on t_job (job_name);

create trigger tr_update_job_last_updated
before update on t_job 
for each row
execute procedure fn_update_timestamp();