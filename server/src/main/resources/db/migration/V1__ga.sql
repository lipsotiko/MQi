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
	patient_id bigint primary key,
	record_cnt bigint,
	server_id bigint,
	chunk_id bigint,
	chunk_status int
);

create unique index ux_chunk_patient_server on chunk (patient_id, server_id);

drop table if exists server;
create table if not exists server (
	server_id bigserial primary key,
	server_name varchar(100),
	server_port varchar(5),
	system_type varchar(50),
	system_version varchar(8),
	chunk_size integer default 1000,
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
	job_status int,
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

create or replace function fn_chunk_data()
returns Integer as $$
declare patient_cnt_desc bigint;
declare patient_cnt_asc bigint;
declare chunk_size_sum bigint;
declare total_record_cnt bigint;
declare server_cursor refcursor;
declare server_rec record;
declare v_server_id bigint;
declare v_chunk_size bigint;
begin
        --Retrieve patients that need to be processed based on the measures in the job
        drop table if exists tmp_patient;
        create local temporary table tmp_patient as
        select p.patient_id
        from patient p
        join measure m
        on 1=1
        inner join job_measure jm
        on jm.measure_id = m.measure_id
        inner join job j
        on j.job_id = jm.job_id
        and j.job_status = ( select job_status_id
                            from job_status
                            where job_status = 'RUNNING')
        left join log_patient_measure lpm
        on p.patient_id = lpm.patient_id
        and lpm.measure_id = m.measure_id
        where m.last_updated > coalesce(lpm.last_updated, '1900-01-01') --if the measure was updated since it was last executed
        or p.last_updated > coalesce(lpm.last_updated, '1900-01-01'); --if the patient was updated since they were last processed

        --temp table to hold each patients total record count
        drop table if exists tmp_patient_record_cnt;
        create local temporary table tmp_patient_record_cnt as
        select patient_id, count(*) as record_cnt, row_number() over(order by count(*) asc) as id
        from (
                select patient_id from patient where patient_id in (select patient_id from tmp_patient)
                union all
                select patient_id from visit where patient_id in (select patient_id from tmp_patient)
                union all
                select patient_id from visit v inner join visit_code vc
			on v.visit_id = vc.visit_id where v.patient_id in (select patient_id from tmp_patient)
        ) p
        group by patient_id;

	--divide patients among all available MQi application servers; larger chunk sizes means the server has more resources
	total_record_cnt := (select sum(record_cnt) from tmp_patient_record_cnt);
	raise info 'total_record_cnt: %', total_record_cnt;

	chunk_size_sum :=  (select sum(chunk_size) from server);
	raise info 'chunk_size_sum: %', chunk_size_sum;

	--use the chunk size to determin a percentage of records each server will process
	drop table if exists tmp_server_record_allocation;
	create local temporary table tmp_server_record_allocation as
	select server_id, chunk_size, ceiling((round(cast(chunk_size as numeric)/chunk_size_sum,2) * total_record_cnt)) as record_cnt
	from server;

	--use a cursor to allocate records to each server based on their determined record count
	--and divide the records into chunks based on their chunk_size
	truncate table chunk;
	open server_cursor no scroll for select server_id, chunk_size, record_cnt from tmp_server_record_allocation;
	loop
		fetch server_cursor into server_rec;
		exit when not found;

		raise notice 'server_id %', server_rec.server_id;
		raise notice 'chunk_size %', server_rec.chunk_size;
		raise notice 'record_cnt %', server_rec.record_cnt;

		--create a running total of record counts
		drop table if exists tmp_patient_record_cnt_running_total;
		create local temporary table tmp_patient_record_cnt_running_total as
		select patient_id, record_cnt, id, sum(record_cnt) over(order by id asc) as running_total
		from tmp_patient_record_cnt
		order by id asc;

		--find records that fit within the chunk_size based on the running total
		--this is not perfect becasue a couple extra members may end up in each chunk;
		drop table if exists tmp_chunk;
		create local temporary table tmp_chunk as
		select patient_id, record_cnt, id, running_total, ceiling(running_total/server_rec.chunk_size) as chunk_id
		from tmp_patient_record_cnt_running_total
		where running_total < server_rec.chunk_size
		and record_cnt < server_rec.chunk_size
		order by id asc;

		insert into chunk (patient_id, record_cnt, server_id, chunk_id)
		select patient_id, record_cnt, server_rec.server_id, chunk_id from tmp_chunk;

		delete from tmp_patient_record_cnt where patient_id in (select patient_id from tmp_chunk);

	end loop;
	close server_cursor;

	--despite our best attempt to spread the patients evenly accross all servers, this is not a perfect process
	--we will append any patients that did not make it on to one of the servers, each in their own chunk.
	--Based on the ordering of patient records earlier in this process, these patients will have the highest
	--record counts which is why they are giving individual chunk ids

	--pick a server that is suitable for the remaining patients
	v_chunk_size := (select max(chunk_size) from server);
	v_server_id := (select server_id from server where chunk_size = v_chunk_size limit 1);

	insert into chunk (patient_id, record_cnt, server_id, chunk_id)
	select patient_id
		, record_cnt
		, v_server_id
		, ROW_NUMBER() over (order by id) + (select max(coalesce(chunk_id,0)) from chunk where server_id = v_server_id) as chunk_id
	from tmp_patient_record_cnt
	where record_cnt <= v_chunk_size;

	--patients that had a record count which exceeded the maximum chunk size will be logged for users to review
	delete from patient_exclude where reason = 'exceeded chunk size for MQi servers';
	insert into patient_exclude (patient_id, reason)
	select patient_id, 'exceeded chunk size for MQi servers' as reason
	from tmp_patient_record_cnt
	where patient_id not in (select patient_id from chunk);

	--users may choose to exlude patients by adding records manually to t_patient_exclude
	delete from chunk where patient_id in (select patient_id from patient_exclude);

	-- return value of 0 = successStepId
	-- return value of -1 = failureStepId
	return 0;

end;
$$ language plpgsql;

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
