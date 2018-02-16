drop table if exists t_patient;
create table if not exists t_patient (
    patient_id bigserial primary key,
    date_of_birth timestamp,
    first_name varchar(255),
    gender char(1) not null,
    last_name varchar(255),
    middle_name varchar(255),
    last_updated timestamp default current_timestamp
);

create trigger tr_update_patient_last_updated
before update on t_patient 
for each row
execute procedure fn_update_timestamp();