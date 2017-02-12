create table batch_task_status (
	id bigint identity not null,
	name varchar(200) not null,
	status varchar(20) not null,
	error_description varchar(300) default null,
	additional_info text default null,
	porcentage int not null,
	creation_date datetime not null,
	modification_date datetime not null,
	constraint batch_task_status_pk primary key (id),
	constraint batch_task_status_name_key unique (name)
);

create index external_debt_idx on deuda (external_id, tipo_item, tipo_pago);

create index branch_code_idx on branchs (client_id, code);