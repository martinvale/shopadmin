insert into features (name) values ('pause_order');

insert into roles_features (feature_id, role_id) values ((select id from feature where name = 'pause_order'),
  (select id from roles where name = 'Editor'));
GO

alter table dbo.PROVEEDORES add banco nvarchar(200) default null;
GO

alter table items_orden add debt_id bigint default null;
alter table items_orden add constraint fk_item_orden_debt_id foreign key (debt_id) references deuda(id);
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
  'mcd' as tipoItem,
  mcdonalds.dbo.Vista_Visitas_Adjuntos.FECHA as fecha,
  CASE Tipos_Pago.id 
	When 1 then mcdonalds.dbo.Vista_Visitas_Adjuntos.Honorarios 
	When 2 then mcdonalds.dbo.Vista_Visitas_Adjuntos.Reintegros 
	when 3 then mcdonalds.dbo.Vista_Visitas_Adjuntos.OtrosGastos 
  end as importe, 
  mcdonalds.dbo.Vista_Visitas_Adjuntos.DOCUMENTO as documento,
  mcdonalds.dbo.Vista_Visitas_Adjuntos.ASIGNACION as externalId,
  CASE ordenes.estado
    WHEN 4 then 'pagada'
    ELSE 'asignada'
  END as estado,
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
  left join ordenes on (
       items_orden.orden_nro = ordenes.numero
  )
WHERE
  items_orden.asignacion is not null and
  (CASE Tipos_Pago.id 
	When 1 then mcdonalds.dbo.Vista_Visitas_Adjuntos.Honorarios 
	When 2 then mcdonalds.dbo.Vista_Visitas_Adjuntos.Reintegros 
	when 3 then mcdonalds.dbo.Vista_Visitas_Adjuntos.OtrosGastos 
  end > 0)
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
  CASE ordenes.estado
    WHEN 4 then 'pagada'
    ELSE 'asignada'
  END as estado,
  null, null,
  (select top 1 clients.id from clients where clients.name = Organizaciones.RazonSocial collate Modern_Spanish_CI_AS),
  Organizaciones.RazonSocial as client, 
  (select top 1 branchs.id from branchs inner join clients on (clients.id = branchs.client_id) where clients.name = Organizaciones.RazonSocial collate Modern_Spanish_CI_AS and branchs.address = PuntosVenta.Nombre collate Modern_Spanish_CI_AS),
  PuntosVenta.Nombre as branch,
  Programas.Nombre + ' - ' + Fases.Nombre AS survey,
  Usuarios_GrupoPuntosVenta.Fecha AS fechaCreacion,
  Usuarios_GrupoPuntosVenta.Fecha AS fechaModificacion
FROM dbo.Tipos_Pago cross join dbo.Usuarios_GrupoPuntosVenta
  left outer join items_orden on ((Usuarios_GrupoPuntosVenta.IdShop = items_orden.asignacion) and (ITEMS_ORDEN.TIPO_PAGO = dbo.Tipos_Pago.id) and (items_orden.tipo_item = 4))
  left join ordenes on (items_orden.orden_nro = ordenes.numero)
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
	(
       (Usuarios_GrupoPuntosVenta.PagaReintegro <> 0) or
       (Usuarios_GrupoPuntosVenta.PagaHonorarios<>0)
     );
GO

*/