update deuda
set deuda.branch_id = b2.id
from deuda
	inner join branchs on (deuda.branch_id = branchs.id)
	inner join ImportacionShopmetrics on (deuda.external_id = ImportacionShopmetrics.InstanceId)
	inner join branchs b2 on (ImportacionShopmetrics.Loc_City = b2.city and ImportacionShopmetrics.Loc_Address = b2.address)
where tipo_item = 'shopmetrics' and branchs.address <> ImportacionShopmetrics.Loc_Address
