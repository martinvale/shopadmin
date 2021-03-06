delete from items_orden where orden_nro is null;

alter table items_orden add marked bit;

select debt_id from items_orden where marked <> 1 group by debt_id having count(debt_id) > 1;

delete from items_orden where marked = 1;

alter table items_orden drop column marked;

alter table items_orden add constraint debt_id_unique UNIQUE (debt_id);

drop table shoppers

create table shoppers (
	id bigint identity not null,
	original_id bigint not null,
	surname varchar(300) not null,
	firstName varchar(300) not null,
	address varchar(300) not null,
	neighborhood varchar(100) null,
	region varchar(200) null,
	state varchar(200) not null,
	country bigint not null,
	postal_code varchar(50) null,
	identity_type varchar(10) null,
	identity_id varchar(50) null,
	work_phone varchar(50) null,
	particular_phone varchar(50) null,
	cell_phone varchar(50) null,
	email varchar(50) not null,
	email2 varchar(50) null,
	cobra_sf bit null,
	referrer varchar(50) null,
	observations varchar(300) null,
	birth_date datetime null,
	gender varchar(6) not null,
	education varchar(150) null,
	confidentiality bit not null,
	login_shopmetrics varchar(50) null,
	enabled bit not null,
	creation_date datetime not null,
	last_modification_date datetime not null,
	constraint pk_shopper primary key (id)
--	constraint idx_identity unique (identity_type, identity_id),
--	constraint idx_shopmetrics unique (login_shopmetrics)
)

insert into shoppers (original_id, country, surname, firstName, address,
	region, state, postal_code, identity_id, work_phone,
	particular_phone, cell_phone, email, email2, cobra_sf,
	referrer, observations, birth_date, gender, neighborhood,
	education, enabled, creation_date, last_modification_date,
	identity_type, confidentiality, login_shopmetrics)
select id,
    case when Pais is null then 1 else Pais end, Apellido_y_nombre, '',
    case when Domicilio is null then '' else Domicilio end,
    Localidad,
	case when Provincia is null then '' else Provincia end,
    Codigo_Postal, NRO_Documento, Tel_Lab,
	Tel_Part, Celular, [E-mail], [E-mail2], Cobra_SF,
	Referido, Observaciones,
	case len([Fecha de nacimiento]) when 10 then cast([Fecha de nacimiento] as datetime) else getdate() end,
	case GENERO when 'M' then 'MALE' else 'FEMALE' end,
	BARRIO, NIVEL_ESTUDIOS, case ESTADO when 'ACTIVO' then 1 else 0 end,
    case when FECHA_ALTA is null then getdate() else FECHA_ALTA end,
    case when FECHA_ALTA is null then getdate() else FECHA_ALTA end,
    case when TipoDocumento is null then 'DNI' else '' end, AcuerdoConfidencialidad,
    case Login_Shopmetrics when '' then null else Login_Shopmetrics end
from [mcdonalds].[dbo].[SHOPPERS]
--where ESTADO != 'INACTIVO'


update account
set account.titular_id = shoppers.id
from account
	inner join shoppers on (account.titular_id = shoppers.original_id)
where titular_tipo = 1

update account
set account.billing_id = shoppers.id
from account
	inner join shoppers on (account.billing_id = shoppers.original_id)
where billing_tipo = 1

update ordenes
set ordenes.proveedor = shoppers.id
from ordenes
    inner join shoppers on (ordenes.proveedor = shoppers.original_id)
where ordenes.proveedor_tipo = 1
