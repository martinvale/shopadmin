package com.ibiscus.shopnchek.application.order;

import com.ibiscus.shopnchek.domain.security.Activity;
import com.ibiscus.shopnchek.domain.security.ActivityRepository;
import com.ibiscus.shopnchek.domain.security.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class TransitionOrderCommand implements Command<OrdenPago> {

	private static final Map<Long, Activity.Code> ORDER_CODES = newHashMap();
	static {
		ORDER_CODES.put(OrderState.VERIFICADA, Activity.Code.ORDER_VERIFIED);
		ORDER_CODES.put(OrderState.PAGADA, Activity.Code.ORDER_PAYED);
		ORDER_CODES.put(OrderState.CERRADA, Activity.Code.ORDER_CLOSED);
		ORDER_CODES.put(OrderState.EN_ESPERA, Activity.Code.ORDER_PAUSED);
		ORDER_CODES.put(OrderState.ANULADA, Activity.Code.ORDER_CANCELLED);
		ORDER_CODES.put(OrderState.ABIERTA, Activity.Code.ORDER_REOPENED);
	}

	private OrderRepository orderRepository;

	private ActivityRepository activityRepository;

	private long numero;

	private long stateId;

	private boolean includeComments;

	private String comments;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public OrdenPago execute() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		OrderState state = orderRepository.getOrderState(stateId);

		OrdenPago order = orderRepository.get(numero);
		order.transition(state, includeComments, comments);
		Activity activity = getActivity(order.getNumero(), user, state);
		activityRepository.save(activity);
		return order;
	}

	private Activity getActivity(long ownerId, User user, OrderState state) {
		if (ORDER_CODES.containsKey(state.getId())) {
			return new Activity(ownerId, user, ORDER_CODES.get(state.getId()));
		}
		return null;
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

	public void setActivityRepository(ActivityRepository activityRepository) {
		this.activityRepository = activityRepository;
	}
}
