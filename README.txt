Egia Software Solutions, Inc
Medical Quality Informatics

Prerequisites: 
* A PostgreSQL Database; parameters for the database will 
need to be updated in the following config file: 
	src/main/resources/application-dev.properties

* A table in the database called t_version; the following 
sql statement may be used to create the table:

CREATE TABLE public.t_version
(
  version_id character varying(8) NOT NULL,
  CONSTRAINT t_version_pkey PRIMARY KEY (version_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.t_version
  OWNER TO postgres;

* One record in the t_version table with a value of ‘0.0.0’



