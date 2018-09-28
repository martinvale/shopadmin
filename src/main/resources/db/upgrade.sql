alter table users add email varchar(100) default null;
update users set email = 'fbasigaluz@shopnchek.com.ar' where username = 'fede';

insert into features (name) values ('approve_additional');
insert into roles_features (feature_id, role_id)
    select id, (select id from roles where name = 'Auditor') from features where name = 'approve_additional';

create table activity (
	id bigint identity(1,1) not null,
	owner_id bigint not null,
	author_id bigint not null,
	code varchar(30) not null,
	detail varchar(300) default null,
	creation_time datetime not null,
	constraint pk_activity primary key (id),
	constraint fk_owner foreign key (owner_id) references ordenes(numero),
	constraint fk_author foreign key (author_id) references users(id)
);
