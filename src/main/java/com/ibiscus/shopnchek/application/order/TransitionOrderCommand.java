package com.ibiscus.shopnchek.application.order;

import com.ibiscus.shopnchek.application.email.CommunicationService;
import com.ibiscus.shopnchek.domain.debt.Debt;
import com.ibiscus.shopnchek.domain.security.Activity;
import com.ibiscus.shopnchek.domain.security.ActivityRepository;
import com.ibiscus.shopnchek.domain.security.User;
import com.ibiscus.shopnchek.domain.security.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
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

	private UserRepository userRepository;

	private CommunicationService communicationService;

	private String from;

	private List<String> usernamesToNotify;

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
		if (state.getId() == OrderState.ABIERTA) {
			sendMail(order, user);
		}
		return order;
	}

	private void sendMail(OrdenPago order, User user) {
		Iterable<User> users = getUserToSendNotification();
		String emailContent = getEmailBody(order, user);
		for (User userToNotify : users) {
			communicationService.sendMail(from, userToNotify.getEmail(), "Order de pago Nro " + order.getNumero()
					+ " reabierta", emailContent);
		}
	}

	private String getEmailBody(OrdenPago order, User user) {
		StringBuilder builder = new StringBuilder("La orden de pago Nro ");
		builder.append(order.getNumero());
		builder.append(" ha sido reabierta por ");
		builder.append(user.getName());
		return builder.toString();
	}

	private Iterable<User> getUserToSendNotification() {
		List<User> users = newArrayList();
		for (String username : usernamesToNotify) {
			users.add(userRepository.findByUsername(username));
		}
		return users;
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

	public void setCommunicationService(CommunicationService communicationService) {
		this.communicationService = communicationService;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void setUsernamesToNotify(List<String> usernamesToNotify) {
		this.usernamesToNotify = usernamesToNotify;
	}
}
