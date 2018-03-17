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