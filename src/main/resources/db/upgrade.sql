alter table ordenes add fecha_pago_confirmada bit not null default 0;

update ordenes set fecha_pago_confirmada = 1 where estado = 4;