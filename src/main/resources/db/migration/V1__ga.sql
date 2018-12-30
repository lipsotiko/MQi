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
    id bigserial primary key,
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
    code_system integer,
    code_value varchar(15)
);

drop table if exists measure;
create table if not exists measure (
	measure_id bigserial primary key,
	measure_name varchar(100) not null,
	measure_json text,
	last_updated timestamp not null
);

--create unique index ux_measure on measure (LOWER(measure_name));

drop table if exists chunk;
create table if not exists chunk (
    chunk_id bigserial primary key,
	patient_id bigint,
	record_count bigint,
	chunk_status integer,
	chunk_group integer
);

create unique index ux_chunk_patient on chunk (patient_id);

drop table if exists job;
create table if not exists job (
	id bigserial primary key,
	job_status integer,
	start_time timestamp,
	end_time timestamp,
	initial_patient_count bigint,
	processed_patient_count bigint,
	last_updated timestamp default current_timestamp
);

create trigger tr_update_job_last_updated
before update on job
for each row
execute procedure fn_update_timestamp();

drop table if exists job_measure_ids;
create table if not exists job_measure_ids (
	id bigserial primary key,
	job_id bigint,
	measure_ids bigint
);

create unique index ux_job_measure_ids on job_measure_ids (job_id, measure_ids);

drop table if exists rule_param;
create table if not exists rule_param (
	rule_param_id bigserial primary key,
	rule_name varchar(255),
	param_name varchar(50),
	param_type varchar(50)
);

drop view if exists code_set;
create table code_set (
    id bigserial primary key,
	code_set_group_id bigint,
	code_system integer,
    code_value varchar(255)
);

drop table if exists code_set_group;
create table if not exists code_set_group (
	id bigserial primary key,
	group_name varchar(255)
);

drop table if exists measure_result;
create table if not exists measure_result (
	id bigserial primary key,
	patient_id bigint,
	measure_id bigint,
	result_code varchar(255)
);

drop table if exists job_status;
create table if not exists job_status (
	id serial primary key,
	job_status varchar(255)
);

insert into job_status (id, job_status)
values(0, 'PENDING'),
    (1, 'RUNNING'),
    (2, 'PROCESSED'),
    (3, 'FAILURE');

drop table if exists code_system;
create table if not exists code_system (
	id serial primary key,
	code_system varchar(255)
);

insert into code_system (id, code_system)
values (0, 'ICD_9'),
    (1, 'ICD_10'),
    (2, 'POS'),
    (3, 'REV'),
    (4, 'TOB'),
    (5, 'DRG'),
    (6, 'HCPCS'),
    (7, 'LOINC'),
    (8, 'CPT2'),
    (9, 'SNOMED');


drop view if exists patient_record_count;
create view patient_record_count as
select a.patient_id
    , max(a.last_updated) as last_updated
    , count(*) as record_count
from (
	select patient_id, last_updated from patient union all
	select patient_id, null from visit union all
	select patient_id, null from visit v inner join visit_code vc on v.visit_id = vc.visit_id ) a
join measure m
    on 1=1
join job_measure_ids jm
    on m.measure_id = jm.measure_ids
    and jm.id = (select max(id) from job_measure_ids)
left join patient_measure_log lpm
    on a.patient_id = lpm.patient_id
    and lpm.measure_id = m.measure_id
where (m.last_updated > coalesce(lpm.last_updated, '1900-01-01') --if the measure was updated since it was last executed
    or a.last_updated > coalesce(lpm.last_updated, '1900-01-01')) --if the patient was updated since they were last processed
group by a.patient_id
order by record_count desc, a.patient_id desc;

insert into measure (measure_name, measure_json, last_updated)
values ('ABC','
    {
      "description": "Patients that are between the ages of 28 and 32 with an occurrence of an ACUTE INPATIENT or MENTAL HEALTH visit.",
      "minimumSystemVersion": "1.0.0",
      "steps": [
      {
        "stepId": 100,
        "ruleName": "AgeWithinDateRange",
        "parameters": [
          {
            "ruleParamId": null,
            "ruleName": "AgeWithinDateRange",
            "paramName": "FROM_AGE",
            "paramType": "INTEGER",
            "paramValue": "28"
          },
          {
            "ruleParamId": null,
            "ruleName": "AgeWithinDateRange",
            "paramName": "TO_AGE",
            "paramType": "INTEGER",
            "paramValue": "32"
          },
          {
            "ruleParamId": null,
            "ruleName": "AgeWithinDateRange",
            "paramName": "START_DATE",
            "paramType": "DATE",
            "paramValue": "19880428"
          },
          {
            "ruleParamId": null,
            "ruleName": "AgeWithinDateRange",
            "paramName": "END_DATE",
            "paramType": "DATE",
            "paramValue": "19880428"
          }
        ],
        "successStepId": 200,
        "failureStepId": 99999
      },
      {
        "stepId": 200,
        "ruleName": "HasCodeSet",
        "parameters": [
          {
            "ruleParamId": null,
            "ruleName": "HasCodeSet",
            "paramName": "CODE_SET",
            "paramType": "TEXT",
            "paramValue": "ACUTE INPATIENT"
          }
        ],
        "successStepId": 400,
        "failureStepId": 300
      },
      {
        "stepId": 300,
        "ruleName": "HasCodeSet",
        "parameters": [
          {
            "ruleParamId": null,
            "ruleName": "HasCodeSet",
            "paramName": "CODE_SET",
            "paramType": "TEXT",
            "paramValue": "MENTAL HEALTH"
          }
        ],
        "successStepId": 400,
        "failureStepId": 99999
      },
      {
        "stepId": 400,
        "ruleName": "SetResultCode",
        "parameters": [
          {
            "ruleParamId": null,
            "ruleName": "SetResultCode",
            "paramName": "RESULT_CODE",
            "paramType": "TEXT",
            "paramValue": "DENOMINATOR"
          }
        ],
        "successStepId": 99999,
        "failureStepId": 99999
      },
      {
        "stepId": 500,
        "ruleName": "ExitMeasure",
        "parameters": [],
        "successStepId": 99999,
        "failureStepId": 99999
      }
      ]
   }'
  , current_timestamp);
