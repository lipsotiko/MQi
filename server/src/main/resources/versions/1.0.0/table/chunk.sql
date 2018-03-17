drop table if exists chunk;
create table if not exists chunk (
	patient_id bigint primary key,
	record_cnt bigint,
	server_id bigint,
	chunk_id bigint
);

create unique index ux_chunk_patient_server on chunk (patient_id, server_id);