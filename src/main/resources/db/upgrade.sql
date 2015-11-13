--Actualizo la deuda
delete from deuda where tipo_item = 'manual' and (fecha <= '2015-10-30 10:00:00' or fecha >= '2015-11-30 10:00:00');

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
where (opnro is null or opnro = 0) and fecha_visita <= '2015-10-30 10:00:00';
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
where ordenes.estado <> 4 and fecha_visita <= '2015-10-30 10:00:00';
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
where ordenes.estado = 4 and fecha_visita <= '2015-10-30 10:00:00';
GO

insert into deuda (tipo_pago, tipo_item, fecha, importe, shopper_dni, external_id, estado,
  usuario, observaciones, client_id, client_description, branch_id, branch_description, 
  survey, fecha_creacion, fecha_modificacion)
SELECT 
  CASE Tipos_Pago.id 
	When 1 then 'honorarios' 
	When 2 then 'reintegros'
	when 3 then 'otrosgastos'
  end as tipoPago,
  'mcd' as tipoItem,
  mcdonalds.dbo.Vista_Visitas_Adjuntos.FECHA as fecha,
  CASE Tipos_Pago.id 
	When 1 then mcdonalds.dbo.Vista_Visitas_Adjuntos.Honorarios 
	When 2 then mcdonalds.dbo.Vista_Visitas_Adjuntos.Reintegros 
	when 3 then mcdonalds.dbo.Vista_Visitas_Adjuntos.OtrosGastos 
  end as importe, 
  mcdonalds.dbo.Vista_Visitas_Adjuntos.DOCUMENTO as documento,
  mcdonalds.dbo.Vista_Visitas_Adjuntos.ASIGNACION as externalId,
  'pagada' as estado,
  null as usuario, null as observaciones,
  (select top 1 clients.id from clients where clients.name = mcdonalds.dbo.Vista_Visitas_Adjuntos.Empresa collate Modern_Spanish_CI_AS),
  mcdonalds.dbo.Vista_Visitas_Adjuntos.Empresa as client, 
  (select top 1 branchs.id from branchs inner join clients on (clients.id = branchs.client_id) where clients.name = mcdonalds.dbo.Vista_Visitas_Adjuntos.Empresa collate Modern_Spanish_CI_AS and branchs.address = mcdonalds.dbo.Vista_Visitas_Adjuntos.LOCAL collate Modern_Spanish_CI_AS),
  mcdonalds.dbo.Vista_Visitas_Adjuntos.LOCAL as branch,
  mcdonalds.dbo.Vista_Visitas_Adjuntos.SUBCUESTIONARIO as survey,
  mcdonalds.dbo.Vista_Visitas_Adjuntos.FECHA as fechaCreacion,
  mcdonalds.dbo.Vista_Visitas_Adjuntos.FECHA as fechaModification
FROM dbo.Tipos_Pago
  cross join mcdonalds.dbo.Vista_Visitas_Adjuntos
    left outer join items_orden on (
       mcdonalds.dbo.Vista_Visitas_Adjuntos.asignacion = items_orden.asignacion and
       ITEMS_ORDEN.TIPO_PAGO = dbo.Tipos_Pago.id and 
       items_orden.tipo_item = mcdonalds.dbo.Vista_Visitas_Adjuntos.Tipo_item
    )
WHERE
  items_orden.asignacion is not null and 
  (CASE Tipos_Pago.id 
	When 1 then mcdonalds.dbo.Vista_Visitas_Adjuntos.Honorarios 
	When 2 then mcdonalds.dbo.Vista_Visitas_Adjuntos.Reintegros 
	when 3 then mcdonalds.dbo.Vista_Visitas_Adjuntos.OtrosGastos 
  end > 0)
GO

/*insert into deuda (tipo_pago, tipo_item, fecha, importe, shopper_dni, external_id, estado,
  usuario, observaciones, client_id, client_description, branch_id, branch_description, 
  survey, fecha_creacion, fecha_modificacion)
SELECT 
  CASE Tipos_Pago.id 
	When 1 then 'honorarios' 
	When 2 then 'reintegros'
	when 3 then 'otrosgastos'
  end as tipoPago,
  'ingematica' as tipoItem,
  Usuarios_GrupoPuntosVenta.Fecha AS fecha,
  CASE Tipos_Pago.id
    when 1 then GrupoPuntosVenta.Honorarios
    when 2 then isnull(Tickets.ReintegroFijo_1,0) + isnull(Tickets.ReintegroFijo_2,0) +
      isnull(Tickets.ReintegroFijo_3,0) + isnull(Tickets.ReintegroFijo_4,0) +
      isnull(Tickets.ReintegroVariable_1,0) + isnull(Tickets.ReintegroVariable_2,0) +
      isnull(Tickets.ReintegroVariable_3,0) + isnull(Tickets.ReintegroVariable_4,0)
    when 3 then isnull(Tickets.GastosQC_1,0) + isnull(Tickets.GastosQC_2,0) +
      isnull(Tickets.GastosQC_3,0) + isnull(Tickets.GastosQC_4,0)
  end as importe,
  DocumentosIdentidad.numero as documento, Usuarios_GrupoPuntosVenta.IdShop AS asignacion,
  'pagada' as estado, null, null,
  (select top 1 clients.id from clients where clients.name = Organizaciones.RazonSocial collate Modern_Spanish_CI_AS),
  Organizaciones.RazonSocial as client, 
  (select top 1 branchs.id from branchs inner join clients on (clients.id = branchs.client_id) where clients.name = Organizaciones.RazonSocial collate Modern_Spanish_CI_AS and branchs.address = PuntosVenta.Nombre collate Modern_Spanish_CI_AS),
  PuntosVenta.Nombre as branch,
  Programas.Nombre + ' - ' + Fases.Nombre AS survey,
  Usuarios_GrupoPuntosVenta.Fecha AS fechaCreacion,
  Usuarios_GrupoPuntosVenta.Fecha AS fechaModificacion
FROM dbo.Tipos_Pago cross join dbo.Usuarios_GrupoPuntosVenta
  left outer join items_orden on ((Usuarios_GrupoPuntosVenta.IdShop = items_orden.asignacion) and (ITEMS_ORDEN.TIPO_PAGO = dbo.Tipos_Pago.id) and (items_orden.tipo_item = 4))
  INNER JOIN dbo.Personas ON DBO.Usuarios_GrupoPuntosVenta.IdUsuario = DBO.Personas.IdPersona
  INNER JOIN dbo.Actores ON DBO.PERSONAS.IDPERSONA = DBO.ACTORES.IDACTOR
  LEFT OUTER JOIN dbo.DocumentosIdentidad ON DBO.Actores.IdDocumentoIdentidad = DBO.DocumentosIdentidad.IdDocumentoIdentidad
  INNER JOIN dbo.GrupoPuntosVenta ON DBO.Usuarios_GrupoPuntosVenta.IdGrupoPuntoVenta = DBO.GrupoPuntosVenta.IdGrupoPuntoVenta
  INNER JOIN dbo.PuntosVenta ON DBO.GrupoPuntosVenta.IdPuntoVenta = DBO.PuntosVenta.IdPuntoVenta
  INNER JOIN dbo.Grupos ON DBO.GrupoPuntosVenta.IdGrupo = DBO.Grupos.IdGrupo
  INNER JOIN dbo.Fases ON DBO.Grupos.IdFase = DBO.Fases.IdFase
  INNER JOIN dbo.Programas ON DBO.Fases.IdPrograma = DBO.Programas.IdPrograma
  INNER JOIN dbo.Organizaciones ON DBO.Organizaciones.IdOrganizacion = DBO.Programas.IdCliente
  LEFT OUTER JOIN dbo.Tickets ON DBO.Tickets.IdShop = DBO.Usuarios_GrupoPuntosVenta.IdShop
  INNER JOIN Actores AS Actores2 ON PuntosVenta.IdPuntoVenta=Actores2.IdActor 
  LEFT JOIN Actores_Direcciones ON Actores2.IdActor=Actores_Direcciones.IdActor 
  LEFT JOIN Direcciones ON Actores_Direcciones.IdDireccion=Direcciones.IdDireccion 
  LEFT JOIN Provincias ON Provincias.IdProvincia=Direcciones.IdProvincia 
WHERE (Usuarios_GrupoPuntosVenta.Estado = 4) And (Provincias.idpais = 41) And 
	((Usuarios_GrupoPuntosVenta.PagaReintegro <> 0)or(Usuarios_GrupoPuntosVenta.PagaHonorarios<>0));
GO*/

sp_rename ordenes, ordenesant
GO

CREATE TABLE ordenes (
	numero bigint IDENTITY NOT NULL,
	proveedor_tipo tinyint NOT NULL,
	proveedor int NOT NULL,
	chequera_nro nvarchar(30) NULL,
	cheque_nro nvarchar(30) NULL,
	fecha_cheque datetime NULL,
	fecha_pago datetime NOT NULL,
	estado tinyint NOT NULL,
	factura nvarchar(3) NULL,
	factura_nro nvarchar(50) NULL,
	iva_honorarios real NULL,
	localidad nvarchar(15) NULL,
	transferenciap bit NOT NULL,
	transferencia_id nvarchar(15) NULL,
	observaciones nvarchar(max) NULL,
	medio_pago smallint NULL,
	obspshopper nvarchar(max) NULL,
	CONSTRAINT pk_order_id PRIMARY KEY (numero)
)
GO

SET IDENTITY_INSERT ordenes ON;

insert into ordenes (numero, proveedor_tipo, proveedor, chequera_nro, cheque_nro, fecha_cheque,
	fecha_pago, estado, factura, factura_nro, iva_honorarios, localidad, transferenciap,
	transferencia_id, observaciones, medio_pago, obspshopper)
select numero, proveedor_tipo, proveedor, chequera_nro, cheque_nro, fecha_cheque,
	fecha_pago, estado, factura, factura_nro, iva_honorarios, localidad, transferenciap,
	transferencia_id, observaciones, medio_pago, obspshopper from ordenesant;

SET IDENTITY_INSERT ordenes OFF;
GO

delete from deuda where tipo_item = 'shopmetrics' and (fecha <= '30/10/2015 10:00:00' or fecha >= '30/11/2015 10:00:00');

-- Inserto los items pagos de Shopmetrics
insert into deuda (external_id, tipo_item, tipo_pago, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'honorarios', Tax_iD, Honorarios, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pagada', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 1 and items_orden.tipo_item = 5)
where items_orden.id is not null and ImportacionShopmetrics.Honorarios is not null and ImportacionShopmetrics.Honorarios > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO

insert into deuda (external_id, tipo_item, tipo_pago, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'reintegros', Tax_iD, Reintegros, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pagada', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 2 and items_orden.tipo_item = 5)
where items_orden.id is not null and ImportacionShopmetrics.Reintegros is not null and ImportacionShopmetrics.Reintegros > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO

insert into deuda (external_id, tipo_item, tipo_pago, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'otrosgastos', Tax_iD, OtrosGastos, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pagada', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 3 and items_orden.tipo_item = 5)
where items_orden.id is not null and ImportacionShopmetrics.OtrosGastos is not null and ImportacionShopmetrics.OtrosGastos > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO

-- Inserto la deuda de Shopmetrics
insert into deuda (external_id, tipo_item, tipo_pago, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'honorarios', Tax_iD, Honorarios, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pendiente', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 1 and items_orden.tipo_item = 5)
where items_orden.id is null and ImportacionShopmetrics.Honorarios is not null and ImportacionShopmetrics.Honorarios > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO

insert into deuda (external_id, tipo_item, tipo_pago, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'reintegros', Tax_iD, Reintegros, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pendiente', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 2 and items_orden.tipo_item = 5)
where items_orden.id is null and ImportacionShopmetrics.Reintegros is not null and ImportacionShopmetrics.Reintegros > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO

insert into deuda (external_id, tipo_item, tipo_pago, shopper_dni, importe, fecha, observaciones,
  usuario, client_id, branch_id, estado, fecha_creacion, fecha_modificacion)
select ImportacionShopmetrics.instanceid, 'shopmetrics', 'otrosgastos', Tax_iD, OtrosGastos, Date_Time, null, null, clients.id as client_id, (select top 1 branchs.id from branchs where branchs.code = ImportacionShopmetrics.Location) as branch_id,
  'pendiente', Date_Time, Date_Time
from ImportacionShopmetrics
  inner join clients on (clients.name = ImportacionShopmetrics.CLIENTE)
  left join items_orden on (ImportacionShopmetrics.InstanceID = items_orden.asignacion and items_orden.tipo_pago = 3 and items_orden.tipo_item = 5)
where items_orden.id is null and ImportacionShopmetrics.OtrosGastos is not null and ImportacionShopmetrics.OtrosGastos > 0 and ImportacionShopmetrics.OK_PAY = 1;
GO