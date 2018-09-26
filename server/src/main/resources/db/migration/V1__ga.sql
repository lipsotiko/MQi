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
    gender char(1),
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

drop table if exists patient_measure_log;
create table if not exists patient_measure_log (
	patient_id bigint,
	measure_id bigint,
	last_updated timestamp default current_timestamp
);
create unique index ux_patient_measure_log on patient_measure_log (patient_id, measure_id);

create trigger tr_update_patient_measure_log_last_updated
before update on patient_measure_log
for each row
execute procedure fn_update_timestamp();

drop table if exists visit;
create table if not exists visit (
    visit_id bigserial primary key,
    patient_id bigint ,
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
    visit_code_id bigserial primary key,
    visit_id bigint,
    code_system varchar(15),
    code_value varchar(15)
);

drop table if exists measure;
create table if not exists measure (
	measure_id bigserial primary key,
	measure_name varchar(100) not null,
	measure_json text,
	last_updated timestamp not null
);

create unique index ux_measure on measure (LOWER(measure_name));

drop table if exists chunk;
create table if not exists chunk (
    chunk_id bigserial primary key,
	patient_id bigint,
	record_count bigint,
	server_id bigint,
	chunk_status integer
);

create unique index ux_chunk_patient_server on chunk (patient_id, server_id);

drop table if exists server;
create table if not exists server (
	server_id bigserial primary key,
	server_name varchar(100),
	server_port varchar(5),
	system_type integer,
	system_version varchar(8),
	last_updated timestamp default current_timestamp
);
create unique index ux_server_name_port on server (server_name, server_port);

create trigger tr_update_server_last_updated
before update on server
for each row
execute procedure fn_update_timestamp();

drop table if exists job;
create table if not exists job (
	job_id bigserial primary key,
	job_status integer,
	start_time timestamp,
	end_time timestamp,
	last_updated timestamp default current_timestamp
);

create trigger tr_update_job_last_updated
before update on job
for each row
execute procedure fn_update_timestamp();

drop table if exists job_measure;
create table if not exists job_measure (
	job_measure_id bigserial primary key,
	job_id bigint,
	measure_id bigint
);

create unique index ux_job_measure on job_measure (job_id, measure_id);

drop table if exists rule_param;
create table if not exists rule_param (
	rule_param_id bigserial primary key,
	rule_name varchar(255),
	param_name varchar(50),
	param_type varchar(50)
);

drop table if exists job_status;
create table if not exists job_status (
	job_status_id serial primary key,
	job_status varchar(255)
);

insert into job_status (job_status_id, job_status)
values(0, 'PENDING'),
    (1, 'RUNNING'),
    (2, 'SUCCESS'),
    (3, 'FAILURE');

drop view if exists patient_record_count;
create view patient_record_count as
select patient_id
    , min(last_updated) as last_updated
    , count(*) as record_count
from (
	select patient_id, last_updated from patient union all
	select patient_id, null from visit union all
	select patient_id, null from visit v inner join visit_code vc on v.visit_id = vc.visit_id ) a
where patient_id not in (
	select patient_id from chunk
)
group by patient_id
order by record_count desc
limit 10000;

insert into measure (measure_name, measure_json, last_updated)
values ('sample measure 1', '
    {
      "description": "Patients that are two years of age on the first day of the reporting year with an occurrence of a broken leg at some time during the reporting year",
      "minimumSystemVersion": "1.0.0",
      "traceRules": true,
      "steps": [
        {
          "stepId": 100,
          "ruleName": "AgesWithinDateRange",
          "parameters": [
            {
              "paramName": "FROM_AGE",
              "paramValue": "2",
              "paramType": "INTEGER"
            },
            {
              "paramName": "TO_AGE",
              "paramValue": "2",
              "paramType": "INTEGER"
            },
            {
              "paramName": "START_DATE",
              "paramValue": "$ryBegin",
              "paramType": "DATE"
            },
            {
              "paramName": "END_DATE",
              "paramValue": "$ryBegin",
              "paramType": "DATE"
            }
          ],
          "successStepId": 200,
          "failureStepId": 99999
        },
        {
          "stepId": 200,
          "ruleName": "SetResultCode",
          "parameters": [
            {
            "paramName": "RESULT_CODE",
            "paramValue": "DENOMINATOR",
            "paramType": "TEXT"
            }
          ],
          "successStepId": 99999,
          "failureStepId": 99999
        }
      ]
    }
', current_timestamp)
, ('sample measure 2','
    {
      "description": "Patients that are between the ages of 25 and 65 on the 12th day of the reporting year with an occurrence of a broken arm at some time during the reporting year",
      "minimumSystemVersion": "1.0.0",
      "traceRules": true,
      "steps": [
        {
          "stepId": 100,
          "ruleName": "AgesWithinDateRange",
          "parameters": [
            {
              "paramName": "FROM_AGE",
              "paramValue": "25",
              "paramType": "INTEGER"
            },
            {
              "paramName": "TO_AGE",
              "paramValue": "65",
              "paramType": "INTEGER"
            },
            {
              "paramName": "START_DATE",
              "paramValue": "$ryBegin",
              "paramType": "DATE"
            },
            {
              "paramName": "END_DATE",
              "paramValue": "$ryBegin",
              "paramType": "DATE"
            }
          ],
          "successStepId": 200,
          "failureStepId": 99999
        },
        {
          "stepId": 200,
          "ruleName": "SetResultCode",
          "parameters": [
            {
              "paramName": "RESULT_CODE",
              "paramValue": "DENOMINATOR",
              "paramType": "TEXT"
            }
          ],
          "successStepId": 99999,
          "failureStepId": 99999
        }
      ]
    }', current_timestamp);
