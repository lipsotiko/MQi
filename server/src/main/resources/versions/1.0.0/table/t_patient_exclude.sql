drop table if exists t_patient_exclude;
create table if not exists t_patient_exclude (
	patient_id bigint primary key,
	reason varchar(250)
);