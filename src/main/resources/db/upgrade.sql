delete from items_orden where orden_nro is null;

alter table items_orden add marked bit;

select debt_id from items_orden where marked <> 1 group by debt_id having count(debt_id) > 1;

delete from items_orden where marked = 1;

alter table items_orden drop column marked;

alter table items_orden add constraint debt_id_unique UNIQUE (debt_id);

drop table shoppers

create table shoppers (
	id bigint identity not null,
	surname varchar(300) not null,
	firstName varchar(300) not null,
	address varchar(300) not null,
	neighborhood varchar(100) null,
	region varchar(200) null,
	state varchar(200) not null,
	country bigint not null,
	postal_code varchar(50) null,
	identity_type varchar(10) not null,
	identity_id varchar(50) not null,
	work_phone varchar(50) null,
	particular_phone varchar(50) null,
	cell_phone varchar(50) null,
	email varchar(50) not null,
	email2 varchar(50) null,
	cobra_sf bit null,
	referrer varchar(50) null,
	observations varchar(300) null,
	birth_date datetime not null,
	gender varchar(6) not null,
	education varchar(150) null,
	confidentiality bit not null,
	login_shopmetrics varchar(50) null,
	enabled bit not null,
	creation_date datetime not null,
	last_modification_date datetime not null,
	constraint pk_shopper primary key (id),
	constraint idx_identity unique (identity_type, identity_id),
	constraint idx_shopmetrics unique (login_shopmetrics)
)


insert into shoppers (country, surname, firstName, address,
	region, state, postal_code, identity_id, work_phone,
	particular_phone, cell_phone, email, email2, cobra_sf,
	referrer, observations, birth_date, gender, neighborhood,
	education, enabled, creation_date, last_modification_date,
	identity_type, confidentiality, login_shopmetrics)
select Pais, Apellido_y_nombre, '', Domicilio, Localidad,
	Provincia, Codigo_Postal, NRO_Documento, Tel_Lab,
	Tel_Part, Celular, [E-mail], [E-mail2], Cobra_SF,
	Referido, Observaciones,
	case len([Fecha de nacimiento]) when 10 then cast([Fecha de nacimiento] as datetime) else NULL end,
	case GENERO when 'M' then 'MALE' else 'FEMALE' end,
	BARRIO, NIVEL_ESTUDIOS, case ESTADO when 'ACTIVO' then 1 else 0 end,
	FECHA_ALTA, FECHA_ALTA, TipoDocumento, AcuerdoConfidencialidad, Login_Shopmetrics
from [mcdonalds].[dbo].[SHOPPERS]
where
	len(Apellido_y_nombre) > 300
	or len(Tel_Lab) > 50
	or len(Tel_Part) > 50
	or len(Celular) > 50
	or len([E-mail]) > 50
	or len([E-mail2]) > 50
	or len(Observaciones) > 300
	or len(Login_Shopmetrics) > 300
