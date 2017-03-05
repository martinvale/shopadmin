update deuda
set deuda.client_description = clients.name
from deuda
	inner join clients on (deuda.client_id = clients.id)
where deuda.client_description is null;

update deuda
set deuda.client_description = ''
where deuda.client_description is null;

update deuda
set deuda.branch_description = branchs.address
from deuda
	inner join branchs on (deuda.branch_id = branchs.id)
where deuda.branch_description is null;

alter table deuda alter column client_description varchar(255) not null;