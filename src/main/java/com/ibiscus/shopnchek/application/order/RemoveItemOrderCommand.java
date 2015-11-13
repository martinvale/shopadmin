package com.ibiscus.shopnchek.application.order;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;

public class RemoveItemOrderCommand implements Command<Boolean> {

	private OrderRepository orderRepository;

	private DebtRepository debtRepository;

	private long numero;

	private long itemId;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean execute() {
		OrdenPago order = orderRepository.get(numero);

		ItemOrden itemToRemove = null;
		for (ItemOrden itemOrden : order.getItems()) {
			if (itemOrden.getId() == itemId) {
				itemToRemove = itemOrden;
			}
		}
		order.getItems().remove(itemToRemove);
		Debt debt = debtRepository.get(itemToRemove.getAsignacion());
		if (debt != null) {
			debt.release();
		}

		return true;
	}

	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public void setDebtRepository(final DebtRepository debtRepository) {
		this.debtRepository = debtRepository;
	}

	public void setNumero(final long numero) {
		this.numero = numero;
	}

	public void setItemId(final long itemId) {
		this.itemId = itemId;
	}
}
