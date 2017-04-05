create table account (
	id bigint identity(1,1) not null,
	titular_id int not null,
	titular_tipo tinyint not null,
	cuit nvarchar(25) default null,
	factura nvarchar(1) default null,
	banco nvarchar(200) default null,
	cbu nvarchar(100) default null,
	number nvarchar(50) default null,
	constraint pk_account primary key (id)
);

create index idx_titular_account on account(titular_id, titular_tipo);

insert into account (titular_id, titular_tipo, cuit, factura, banco)
	select id, 2, cuit, factura, banco from proveedores;

alter table ordenes add
	cuit nvarchar(25) default null,
	banco nvarchar(200) default null,
	cbu nvarchar(100) default null,
	account_number nvarchar(50) default null;

update ordenes
set ordenes.banco = account.banco, ordenes.cuit = account.cuit
from ordenes
	inner join account on (ordenes.proveedor = account.titular_id and ordenes.proveedor_tipo = 2);

alter table proveedores drop column cuit;
alter table proveedores drop column factura;
alter table proveedores drop column banco;

alter table proveedores add email nvarchar(100) null;