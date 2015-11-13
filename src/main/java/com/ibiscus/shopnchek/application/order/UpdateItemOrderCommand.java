package com.ibiscus.shopnchek.application.order;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;

public class UpdateItemOrderCommand implements Command<Boolean> {

	private OrderRepository orderRepository;

	private long itemOrderId;

	private double importe;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean execute() {
	    ItemOrden itemOrden = orderRepository.getItem(itemOrderId);
	    itemOrden.updateImporte(importe);
		return true;
	}

	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public void setItemOrderId(final long itemOrderId) {
		this.itemOrderId = itemOrderId;
	}

	public void setImporte(final double importe) {
		this.importe = importe;
	}

}
