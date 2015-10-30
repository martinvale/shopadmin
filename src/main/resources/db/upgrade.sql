create table users (
  id bigint not null identity,
  username varchar(100) not null,
  password varchar(100) not null,
  name varchar(255) not null,
  enabled bit not null,
  constraint pk_user primary key (id),
  constraint ak_username unique (username)
);
GO

create table roles (
  id bigint not null identity,
  name varchar(100) not null,
  constraint pk_role primary key (id),
);
GO

create table users_roles (
  user_id bigint not null,
  role_id bigint not null,
  constraint pk_users_roles primary key (user_id, role_id),
  constraint fk_users_roles_user_id foreign key (user_id) references users(id),
  constraint fk_users_roles_role_id foreign key (role_id) references roles(id)
);
GO

create table features (
  id bigint not null identity,
  name varchar(100) not null,
  constraint pk_features primary key (id),
);
GO

create table roles_features (
  feature_id bigint not null,
  role_id bigint not null,
  constraint pk_roles_features primary key (feature_id, role_id),
  constraint fk_roles_features_feature_id foreign key (feature_id) references features(id),
  constraint fk_roles_features_role_id foreign key (role_id) references roles(id)
);
GO

insert into users (username, password, name, enabled)
  select descripcion, password, nombre, 1s from usuarios where nombre is not null;
GO

insert into roles (name) values ('Administrador');
insert into roles (name) values ('Editor');
insert into roles (name) values ('Auditor');
insert into roles (name) values ('Invitado');
GO

insert into users_roles (user_id, role_id)
  select users.id, (select roles.id from roles where name = 'Administrador')
  from users inner join usuarios on (users.username = usuarios.descripcion)
  where perfil = 2;
insert into users_roles (user_id, role_id)
  select users.id, (select roles.id from roles where name = 'Auditor')
  from users inner join usuarios on (users.username = usuarios.descripcion)
  where perfil_autorizacion_adicionales = 2;
insert into users_roles (user_id, role_id)
  select users.id, (select roles.id from roles where name = 'Editor')
  from users inner join usuarios on (users.username = usuarios.descripcion)
  where perfil_modif_estado_ordenes = 2;
insert into users_roles (user_id, role_id)
  select users.id, (select roles.id from roles where name = 'Invitado')
  from users
    inner join usuarios on (users.username = usuarios.descripcion)
    left join users_roles on (users.id = users_roles.user_id)
  where users_roles.user_id is null;
GO

insert into features (name) values ('create_aditional');
insert into features (name) values ('list_aditional');
insert into features (name) values ('edit_order');
GO

insert into roles_features (feature_id, role_id)
  select (select features.id from features where features.name = 'create_aditional'),
    (select roles.id from roles where roles.name = 'Auditor');
insert into roles_features (feature_id, role_id)
  select (select features.id from features where features.name = 'list_aditional'),
    (select roles.id from roles where roles.name = 'Auditor');
insert into roles_features (feature_id, role_id)
  select (select features.id from features where features.name = 'edit_order'),
    (select roles.id from roles where roles.name = 'Editor');
GO

create table clients (
  id bigint not null identity,
  name varchar(255) not null,
  address varchar(255) null,
  country varchar(255) null,
  city varchar(255) null,
  state varchar(255) null,
  postal_code varchar(20) null,
  constraint pk_client primary key (id)
);
GO

create table client_principal (
  id bigint not null,
  source varchar(30) not null,
  client_id bigint not null,
  constraint pk_client_principal primary key (id, source, client_id),
  constraint fk_client foreign key (client_id) references clients(id)
);
GO

insert into clients (name, address, country, city, state, postal_code)
  select ClientName, Address, Country, City, 'State/Region', 'Postal Code'
  from ShopmetricsClientes;
GO

insert into client_principal (id, source, client_id)
  select ShopmetricsClientes.ClientId, 'shopmetrics', clients.id
  from ShopmetricsClientes
    inner join clients on (clients.name = ShopmetricsClientes.ClientName);
GO

insert into clients (name)
select CLIENTE_NOMBRE
from items_adicionales_autorizados
	left join clients on (items_adicionales_autorizados.CLIENTE_NOMBRE = clients.name) 
where clients.id is null
group by CLIENTE_NOMBRE;
GO

insert into client_principal (id, source, client_id)
select CLIENTE, 'iplan' as source, clients.id
from items_adicionales_autorizados
	inner join clients on (items_adicionales_autorizados.CLIENTE_NOMBRE = clients.name) 
where tipo_item = 1
group by CLIENTE, CLIENTE_NOMBRE, clients.id;
GO

insert into client_principal (id, source, client_id)
select CLIENTE, 'mcd' as source, clients.id
from items_adicionales_autorizados
	inner join clients on (items_adicionales_autorizados.CLIENTE_NOMBRE = clients.name) 
where tipo_item = 2
group by CLIENTE, CLIENTE_NOMBRE, clients.id;
GO

insert into client_principal (id, source, client_id)
select CLIENTE, 'manual' as source, clients.id
from items_adicionales_autorizados
	inner join clients on (items_adicionales_autorizados.CLIENTE_NOMBRE = clients.name) 
where tipo_item = 3
group by CLIENTE, CLIENTE_NOMBRE, clients.id;
GO

insert into client_principal (id, source, client_id)
select CLIENTE, 'ingematica' as source, clients.id
from items_adicionales_autorizados
	inner join clients on (items_adicionales_autorizados.CLIENTE_NOMBRE = clients.name) 
where tipo_item = 4
group by CLIENTE, CLIENTE_NOMBRE, clients.id;
GO

insert into clients (name)
select ImportacionShopmetrics.Cliente
from ImportacionShopmetrics
	left join clients on (ImportacionShopmetrics.cliente = clients.name)
where clients.id is null
group by ImportacionShopmetrics.Cliente;
GO

insert into client_principal (id, source, client_id)
select clients.id, 'shopmetrics' as source, clients.id from clients
	left join client_principal on (client_principal.client_id = clients.id)
where client_principal.id is null;
GO

--Actualizo las sucursales
create table branchs (
  id bigint not null identity,
  client_id bigint not null,
  code nvarchar(50) null,
  address nvarchar(255) not null,
  city nvarchar(255) null,
  country nvarchar(255) null,
  constraint pk_branch primary key (id),
  constraint fk_branch_client foreign key (client_id) references clients(id)
);
GO

insert into branchs (client_id, address)
select clients.id, sucursal_nombre
from items_adicionales_autorizados
  left join clients on (clients.name = items_adicionales_autorizados.cliente_nombre)
group by clients.id, sucursal_nombre;
GO

create table deuda (
  id bigint not null identity,
  tipo_pago varchar(20) not null,
  tipo_item varchar(30) not null,
  fecha datetime not null,
  importe float not null,
  shopper_dni varchar(50) null,
  estado varchar(10) not null,
  client_id bigint not null,
  branch_id bigint null,
  usuario varchar(50) null,
  observaciones nvarchar(200) null,
  survey nvarchar(100) null,
  external_id bigint null,
  fecha_creacion datetime null,
  fecha_modificacion datetime null,
  constraint pk_deuda primary key (id),
  constraint fk_deuda_client foreign key (client_id) references clients(id),
  constraint fk_deuda_branch foreign key (branch_id) references branchs(id)
);
GO

--Actualizo la deuda
insert into deuda (tipo_pago, tipo_item, fecha, importe, shopper_dni, estado, usuario,
  observaciones, client_id, branch_id, fecha_creacion, fecha_modificacion)
select
  case tipo_pago
    when 1 then 'honorarios'
    when 2 then 'reintegros'
    when 3 then 'otrosgastos'
  end,
  'manual', fecha_visita, importe, shopper_dni, 'pendiente', usuario_autorizacion,
  observaciones, (select top 1 clients.id from clients where clients.name = items_adicionales_autorizados.cliente_nombre),
  (select top 1 branchs.id from branchs inner join clients on (clients.id = branchs.client_id) where clients.name = items_adicionales_autorizados.cliente_nombre and branchs.address = items_adicionales_autorizados.sucursal_nombre),
  fecha_visita, fecha_visita
from items_adicionales_autorizados
where opnro is null or opnro = 0;
GO

insert into deuda (tipo_pago, tipo_item, fecha, importe, shopper_dni, estado, usuario,
  observaciones, client_id, branch_id, fecha_creacion, fecha_modificacion)
select
  case tipo_pago
    when 1 then 'honorarios'
    when 2 then 'reintegros'
    when 3 then 'otrosgastos'
  end,
  'manual', fecha_visita, importe, shopper_dni, 'asignada', usuario_autorizacion,
  items_adicionales_autorizados.observaciones,
  (select top 1 clients.id from clients where clients.name = items_adicionales_autorizados.cliente_nombre),
  (select top 1 branchs.id from branchs inner join clients on (clients.id = branchs.client_id) where clients.name = items_adicionales_autorizados.cliente_nombre and branchs.address = items_adicionales_autorizados.sucursal_nombre),
  fecha_visita, fecha_visita
from items_adicionales_autorizados
  inner join ordenes on (items_adicionales_autorizados.opnro = ordenes.numero)
where ordenes.estado <> 4;
GO

insert into deuda (tipo_pago, tipo_item, fecha, importe, shopper_dni, estado, usuario,
  observaciones, client_id, branch_id, fecha_creacion, fecha_modificacion)
select
  case tipo_pago
    when 1 then 'honorarios'
    when 2 then 'reintegros'
    when 3 then 'otrosgastos'
  end,
  'manual', fecha_visita, importe, shopper_dni, 'pagada', usuario_autorizacion,
  items_adicionales_autorizados.observaciones,
  (select top 1 clients.id from clients where clients.name = items_adicionales_autorizados.cliente_nombre),
  (select top 1 branchs.id from branchs inner join clients on (clients.id = branchs.client_id) where clients.name = items_adicionales_autorizados.cliente_nombre and branchs.address = items_adicionales_autorizados.sucursal_nombre),
  fecha_visita, fecha_visita
from items_adicionales_autorizados
  inner join ordenes on (items_adicionales_autorizados.opnro = ordenes.numero)
where ordenes.estado = 4;
GO

-- Inserto las sucursales que faltan de Shopmetrics
insert into branchs (client_id, code, address, city)
select clients.id, Location, Loc_Address, Loc_City
from ImportacionShopmetrics
  left join clients on (clients.name = ImportacionShopmetrics.Cliente)
where Loc_Address is not null and Loc_Address <> ''
group by clients.id, Location, Loc_Address, Loc_City;
GO

-- Borro los items duplicados de Shopmetrics
delete from items_orden where id in (select items_orden.id
from items_orden
    left join items_orden items_orden2 on (items_orden.asignacion = items_orden2.asignacion and items_orden.id > items_orden2.id)
where items_orden.asignacion in (
  select items_orden.asignacion
  from items_orden 
    inner join ImportacionShopmetrics on (ImportacionShopmetrics.InstanceID = items_orden.asignacion)
  where items_orden.tipo_pago = 1 and items_orden.tipo_item = 5
  group by items_orden.asignacion
  having count(items_orden.asignacion) > 1
) and items_orden.tipo_pago = 1 and items_orden.tipo_item = 5 and items_orden2.tipo_pago = 1 and items_orden2.tipo_item = 5
)

delete from items_orden where id in (select items_orden.id
from items_orden
    left join items_orden items_orden2 on (items_orden.asignacion = items_orden2.asignacion and items_orden.id > items_orden2.id)
where items_orden.asignacion in (
  select items_orden.asignacion
  from items_orden 
    inner join ImportacionShopmetrics on (ImportacionShopmetrics.InstanceID = items_orden.asignacion)
  where items_orden.tipo_pago = 2 and items_orden.tipo_item = 5
  group by items_orden.asignacion
  having count(items_orden.asignacion) > 1
) and items_orden.tipo_pago = 2 and items_orden.tipo_item = 5 and items_orden2.tipo_pago = 2 and items_orden2.tipo_item = 5
)
GO

-- Inserto los items pagos de Shopmetrics
insert into deuda (external_id, tipo_item, tipo_pago, survey, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'honorarios', Survey, Tax_iD, Honorarios, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pagada', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 1 and items_orden.tipo_item = 5)
where items_orden.id is not null and ImportacionShopmetrics.Honorarios is not null and ImportacionShopmetrics.Honorarios > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO

insert into deuda (external_id, tipo_item, tipo_pago, survey, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'reintegros', Survey, Tax_iD, Reintegros, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pagada', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 2 and items_orden.tipo_item = 5)
where items_orden.id is not null and ImportacionShopmetrics.Reintegros is not null and ImportacionShopmetrics.Reintegros > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO

insert into deuda (external_id, tipo_item, tipo_pago, survey, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'otrosgastos', Survey, Tax_iD, OtrosGastos, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pagada', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 3 and items_orden.tipo_item = 5)
where items_orden.id is not null and ImportacionShopmetrics.OtrosGastos is not null and ImportacionShopmetrics.OtrosGastos > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO

-- Inserto la deuda de Shopmetrics
insert into deuda (external_id, tipo_item, tipo_pago, survey, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'honorarios', Survey, Tax_iD, Honorarios, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pendiente', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 1 and items_orden.tipo_item = 5)
where items_orden.id is null and ImportacionShopmetrics.Honorarios is not null and ImportacionShopmetrics.Honorarios > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO

insert into deuda (external_id, tipo_item, tipo_pago, survey, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'reintegros', Survey, Tax_iD, Reintegros, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pendiente', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 2 and items_orden.tipo_item = 5)
where items_orden.id is null and ImportacionShopmetrics.Reintegros is not null and ImportacionShopmetrics.Reintegros > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO

insert into deuda (external_id, tipo_item, tipo_pago, survey, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'otrosgastos', Survey, Tax_iD, OtrosGastos, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pendiente', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 3 and items_orden.tipo_item = 5)
where items_orden.id is null and ImportacionShopmetrics.OtrosGastos is not null and ImportacionShopmetrics.OtrosGastos > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO

create table feed(
  id bigint not null identity,
  code varchar(50) not null,
  active bit not null,
  last_processed_date datetime null,
  constraint pk_feed primary key (id)  
);
GO

insert into feed (code, active) values ('mcd_items_debt', 1);
insert into feed (code, active) values ('ingematica_items_debt', 1);
GO