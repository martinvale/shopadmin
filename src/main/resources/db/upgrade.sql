alter table ordenes add fecha_creacion datetime not null default getdate();
update ordenes set fecha_creacion = fecha_pago;
