alter table ordenes add fecha_creacion datetime not null default getdate();
alter table ordenes add times_reopened int not null default 0;
update ordenes set fecha_creacion = fecha_pago;
