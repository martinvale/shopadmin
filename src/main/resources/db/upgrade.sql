delete from items_orden where orden_nro is null;

alter table items_orden add marked bit;

select debt_id from items_orden where marked <> 1 group by debt_id having count(debt_id) > 1;

delete from items_orden where marked = 1;

alter table items_orden drop column marked;

alter table items_orden add constraint debt_id_unique UNIQUE (debt_id);



