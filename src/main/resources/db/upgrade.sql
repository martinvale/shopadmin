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
