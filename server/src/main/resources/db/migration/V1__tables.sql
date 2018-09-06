create or replace function fn_update_timestamp()
returns trigger as $$
	begin
		new.last_updated = now();
		return new;
	end;
$$ language 'plpgsql';

drop table if exists patient;
create table if not exists patient (
    patient_id bigserial primary key,
    date_of_birth timestamp,
    first_name varchar(255),
    gender char(1) not null,
    last_name varchar(255),
    middle_name varchar(255),
    last_updated timestamp default current_timestamp
);

create trigger tr_update_patient_last_updated
before update on patient
for each row
execute procedure fn_update_timestamp();

drop table if exists patient_exclude;
create table if not exists patient_exclude (
	patient_id bigint primary key,
	reason varchar(250)
);

drop table if exists log_patient_measure;
create table if not exists log_patient_measure (
	patient_id bigint,
	measure_id bigint,
	last_updated timestamp default current_timestamp
);
create unique index ux_log_patient_measure on log_patient_measure (patient_id, measure_id);

create trigger tr_update_log_patient_measure_last_updated
before update on log_patient_measure
for each row
execute procedure fn_update_timestamp();

drop table if exists visit;
create table if not exists visit (
    visit_id bigserial primary key,
    patient_id bigint not null,
    admit_dt timestamp,
    covered_days integer,
    date_of_service timestamp,
    denied boolean,
    primary_dx_code varchar(255),
    primary_dx_code_version integer,
    discharge_dt timestamp,
    discharge_status integer,
    provider_id varchar(255),
    supplemental boolean,
    units varchar(255)
);

drop table if exists visit_code;
create table if not exists visit_code (
    visit_id bigint primary key,
    code_system varchar(15),
    code_value varchar(15)
);

drop table if exists measure;
create table if not exists measure (
	measure_id serial primary key,
	measure_name varchar(100) not null,
	measure_json text,
	last_updated timestamp not null
);

create unique index ux_measure on measure (LOWER(measure_name));

drop table if exists chunk;
create table if not exists chunk (
	patient_id bigint primary key,
	record_cnt bigint,
	server_id bigint,
	chunk_id bigint
);

create unique index ux_chunk_patient_server on chunk (patient_id, server_id);

drop table if exists server;
create table if not exists server (
	server_id serial primary key,
	server_name varchar(100),
	server_port varchar(5),
	system_type varchar(50),
	system_version varchar(8),
	chunk_size integer default 100,
	last_updated timestamp default current_timestamp
);
create unique index ux_server_name_port on server (server_name, server_port);

create trigger tr_update_server_last_updated
before update on server
for each row
execute procedure fn_update_timestamp();

drop table if exists job;
create table if not exists job (
	job_id serial primary key,
	job_name varchar(255),
	process_type varchar(25) not null,
	order_id integer default 0,
	status varchar(25) default 'idle',
	start_time timestamp,
	end_time timestamp,
	last_updated timestamp default current_timestamp
);

create unique index ux_job on job (job_name);

create trigger tr_update_job_last_updated
before update on job
for each row
execute procedure fn_update_timestamp();

drop table if exists job_measure;
create table if not exists job_measure (
	job_measure_id serial primary key,
	job_id bigint,
	measure_id bigint
);

create unique index ux_job_measure on job_measure (job_id, measure_id);

drop table if exists rule_param;
create table if not exists rule_param (
	rule_param_id serial primary key,
	rule_name varchar(255),
	param_name varchar(50),
	param_type varchar(50)
);
