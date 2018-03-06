drop table if exists t_job_measure;
create table if not exists t_job_measure (
	job_measure_id serial primary key,
	job_id bigint,
	measure_id bigint
);

create unique index ux_t_job_measure on t_job_measure (job_id, measure_id);