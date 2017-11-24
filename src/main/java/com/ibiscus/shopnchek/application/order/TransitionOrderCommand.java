package com.ibiscus.shopnchek.application.order;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;

public class TransitionOrderCommand implements Command<OrdenPago> {

	private OrderRepository orderRepository;

	private long numero;

	private long stateId;

	private boolean includeComments;

	private String comments;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public OrdenPago execute() {
		OrderState state = orderRepository.getOrderState(stateId);

		OrdenPago order = orderRepository.get(numero);
		order.transition(state, includeComments, comments);
		return order;
	}

	public void setOrderRepository(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public void update(long numero, long stateId, String comments) {
	    this.numero = numero;
	    this.stateId = stateId;
	    this.comments = comments;
	    this.includeComments = true;
	}

    public void update(long numero, long stateId) {
        this.numero = numero;
        this.stateId = stateId;
        this.includeComments = false;
    }
}
