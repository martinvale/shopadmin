package com.ibiscus.shopnchek.application.order;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ibiscus.shopnchek.application.SearchCommand;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;

public class SearchOrderDtoCommand extends SearchCommand<OrderDto> {

	private OrderRepository orderRepository;

	private Integer tipoTitular;

	private Integer titularId;

	private String dniShopper;

	private String numeroCheque;

	private Long stateId;

	public SearchOrderDtoCommand() {
	}

	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public void setTipoTitular(final Integer tipoTitular) {
		this.tipoTitular = tipoTitular;
	}

	public void setTitularId(final Integer titularId) {
		this.titularId = titularId;
	}

	public void setDniShopper(final String dniShopper) {
		this.dniShopper = dniShopper;
	}

	public void setNumeroCheque(final String numeroCheque) {
		this.numeroCheque = numeroCheque;
	}

	public void setStateId(final Long stateId) {
		this.stateId = stateId;
	}

	@Override
	protected List<OrderDto> getItems() {
		List<OrderDto> orderDtoItems = new ArrayList<OrderDto>();

		List<OrdenPago> orderItems = orderRepository.find(getStart(), getPageSize(),
				getOrderBy(), isAscending(), tipoTitular, titularId, dniShopper,
				numeroCheque, getState());
		for (OrdenPago order : orderItems) {
			OrderDto orderDto = null;
			if (!StringUtils.isBlank(dniShopper)) {
				double importe = 0;
				for (ItemOrden itemOrden : order.getItems()) {
					if (dniShopper.equals(itemOrden.getShopperDni())) {
						importe = importe + itemOrden.getImporte();
					}
				}
				orderDto = new OrderDto(order, importe);
			} else {
				orderDto = new OrderDto(order);
			}
			orderDtoItems.add(orderDto);
		}
		return orderDtoItems;
	}

	@Override
	protected int getCount() {
		return orderRepository.getCount(tipoTitular, titularId, dniShopper,
				numeroCheque, getState());
	}

	private OrderState getState() {
		OrderState state = null;
		if (stateId != null) {
			state = orderRepository.getOrderState(stateId);
		}
		return state;
	}
}
