create table configuration
(
	id bigint auto_increment
		primary key,
	name varchar(127) not null,
	factory_class varchar(255) not null,
	attributes text null,
	constraint configuration_id_uindex
		unique (id),
	constraint configuration_name_uindex
		unique (name)
)
engine=InnoDB
;

create table parameter
(
	id bigint auto_increment
		primary key,
	name varchar(127) null,
	optional_fragment text null,
	type varchar(255) null
)
engine=InnoDB
;

create table query
(
	id bigint auto_increment
		primary key,
	description varchar(255) null,
	name varchar(127) null,
	template text null,
	configuration_id bigint null,
	constraint query_name_uindex
		unique (name),
	constraint query_configuration_id_fk
		foreign key (configuration_id) references configuration (id)
)
engine=InnoDB
;

create index query_configuration_id_fk
	on query (configuration_id)
;

create table query_parameter
(
	query_id bigint not null,
	parameter_id bigint not null,
	primary key (query_id, parameter_id),
	constraint query_parameter_id_uindex
		unique (parameter_id),
	constraint query_parameter_query_id_fk
		foreign key (query_id) references query (id),
	constraint query_parameter_parameter_id_fk
		foreign key (parameter_id) references parameter (id)
)
engine=InnoDB
;
