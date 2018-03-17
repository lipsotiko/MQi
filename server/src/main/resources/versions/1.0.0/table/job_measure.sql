drop table if exists job_measure;
create table if not exists job_measure (
	job_measure_id serial primary key,
	job_id bigint,
	measure_id bigint
);

create unique index ux_job_measure on job_measure (job_id, measure_id);