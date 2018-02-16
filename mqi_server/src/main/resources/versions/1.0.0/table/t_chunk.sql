drop table if exists t_chunk;
create table if not exists t_chunk (
	patient_id bigint primary key,
	record_cnt bigint,
	server_id bigint,
	chunk_id bigint
);

create unique index ux_t_chunk_patient_server on t_chunk (patient_id, server_id);