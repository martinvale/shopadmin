alter table deuda add client_description varchar(255) default null;
alter table deuda add branch_description varchar(255) default null;
alter table deuda alter column client_id bigint null;
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
where opnro is null or opnro = 0 and fecha_visita >= '2015-10-30 10:00:00' and fecha_visita <= '2015-11-05 10:00:00';
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
where ordenes.estado <> 4 and fecha_visita >= '2015-10-30 10:00:00' and fecha_visita <= '2015-11-05 10:00:00';
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
where ordenes.estado = 4 and fecha_visita >= '2015-10-30 10:00:00' and fecha_visita <= '2015-11-05 10:00:00';
GO
