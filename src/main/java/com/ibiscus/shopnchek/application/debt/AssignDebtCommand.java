package com.ibiscus.shopnchek.application.debt;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.application.orden.ItemsOrdenService;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import com.ibiscus.shopnchek.domain.admin.TipoPago;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;
import com.ibiscus.shopnchek.domain.debt.TipoItem;
import com.ibiscus.shopnchek.domain.security.User;

public class AssignDebtCommand implements Command<Boolean> {

	private DebtRepository debtRepository;

	private OrderRepository orderRepository;

	private ShopperRepository shopperRepository;

	private ItemsOrdenService itemsOrdenService;

	private List<Long> debtIds;

	private long orderId;

	private User operator;

	public AssignDebtCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean execute() {
		Validate.isTrue(orderId > 0, "The order id must be greater than 0");
		OrdenPago order = orderRepository.get(orderId);
		for (Long debtId : debtIds) {
			Debt debt = debtRepository.get(debtId);

			Shopper shopper = shopperRepository.findByDni(debt.getShopperDni());

			TipoPago unTipoPago;
			if (debt.getTipoPago() == com.ibiscus.shopnchek.domain.debt.TipoPago.honorarios) {
				unTipoPago = orderRepository.getTipoPago(1);
			} else if (debt.getTipoPago() == com.ibiscus.shopnchek.domain.debt.TipoPago.reintegros) {
				unTipoPago = orderRepository.getTipoPago(2);
			} else {
				unTipoPago = orderRepository.getTipoPago(3);
			}

			int tipoItem = 0;
			if (debt.getTipoItem() == TipoItem.iplan) {
				tipoItem = 1;
			} else if (debt.getTipoItem() == TipoItem.mcd) {
				tipoItem = 2;
			} else if (debt.getTipoItem() == TipoItem.manual) {
				tipoItem = 3;
			} else if (debt.getTipoItem() == TipoItem.ingematica) {
				tipoItem = 4;
			} else if (debt.getTipoItem() == TipoItem.shopmetrics) {
				tipoItem = 5;
			}

			Long newId = itemsOrdenService.getItemOrdenId();
			String cliente = debt.getClientDescription();
			if (debt.getClient() != null) {
				cliente = debt.getClient().getName();
			}
			if (cliente != null && cliente.length() > ItemOrden.CLIENTE_FIELD_LENGTH) {
				cliente = cliente.substring(0, ItemOrden.CLIENTE_FIELD_LENGTH);
			}
			String sucursal = debt.getBranchDescription();
			if (debt.getBranch() != null) {
				sucursal = debt.getBranch().getAddress();
			}
			if (sucursal != null && sucursal.length() > ItemOrden.SUCURSAL_FIELD_LENGTH) {
				sucursal = sucursal.substring(0, ItemOrden.SUCURSAL_FIELD_LENGTH);
			}
			String description = null;
			if (debt.getTipoItem() == TipoItem.manual && debt.getExternalId() != null) {
				description = itemsOrdenService.getObservacionAdicional(debt.getExternalId());
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(debt.getFecha());
			int mes = calendar.get(Calendar.MONTH) + 1;
			int year = calendar.get(Calendar.YEAR);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			ItemOrden itemOrden = new ItemOrden(newId, order, operator.getId(),
					debt.getShopperDni(), new Long(debt.getId()).intValue(), tipoItem,
					unTipoPago, cliente, sucursal, mes, year,
					dateFormat.format(debt.getFecha()), debt.getImporte(), description, 1);
			itemOrden.updateShopper(shopper);
			orderRepository.saveItem(itemOrden);
			/*if (debt.getTipoItem() == TipoItem.manual && asignacion != null) {
				itemsOrdenService.linkAdicional(asignacion, order.getNumero());
			}*/
			debt.pagado();
		}
		return true;
	}

	public void setDebtRepository(final DebtRepository debtRepository) {
		this.debtRepository = debtRepository;
	}

	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public void setShopperRepository(final ShopperRepository shopperRepository) {
		this.shopperRepository = shopperRepository;
	}

	public void setItemsOrdenService(final ItemsOrdenService itemsOrdenService) {
		this.itemsOrdenService = itemsOrdenService;
	}

	public void setDebtIds(final List<Long> debtIds) {
		this.debtIds = debtIds;
	}

	public void setOrderId(final long orderId) {
		this.orderId = orderId;
	}

	public void setOperator(final User operator) {
		this.operator = operator;
	}
}
