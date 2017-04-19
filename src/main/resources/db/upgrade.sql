alter table ordenes add notified bit not null default 0;

alter table account add linked bit not null default 0;
alter table account add billing_id int;
alter table account add billing_tipo tinyint;
