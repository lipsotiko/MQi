drop table if exists server;
create table if not exists server (
	server_id serial primary key,
	server_name varchar(100),
	server_port varchar(5),
	server_type varchar(50),
	server_version varchar(8),
	chunk_size integer default 100,
	last_updated timestamp default current_timestamp
);
create unique index ux_server_name_port on server (server_name, server_port);

create trigger tr_update_server_last_updated
before update on server
for each row
execute procedure fn_update_timestamp();