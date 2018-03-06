drop table if exists t_server;
create table if not exists t_server (
	server_id serial primary key,
	server_name varchar(100),
	server_port varchar(5),
	server_type varchar(50),
	server_version varchar(8),
	chunk_size integer default 100,
	last_updated timestamp default current_timestamp
);
create unique index ux_t_server_name_port on t_server (server_name, server_port);

create trigger tr_update_t_server_last_updated
before update on t_server
for each row
execute procedure fn_update_timestamp();