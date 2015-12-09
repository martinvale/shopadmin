package com.ibiscus.shopnchek.application.order;

import java.util.Date;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.MedioPago;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;
import com.ibiscus.shopnchek.domain.debt.DebtRepository;

public class PayOrderCommand implements Command<OrdenPago> {

	private OrderRepository orderRepository;

	private DebtRepository debtRepository;

	private long numero;

	private long medioPagoId;

	private String numeroChequera;

	private String numeroCheque;

	private Date fechaCheque;

	private String idTransferencia;

	private String observaciones;

	private String observacionesShopper;

	private Integer stateId;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public OrdenPago execute() {
		MedioPago medioPago = orderRepository.getMedioPago(medioPagoId);
		OrderState state = null;
		/*if (medioPago.getId() == 3) {
			state = orderRepository.getOrderState(OrderState.CERRADA);
		} else {
			state = orderRepository.getOrderState(OrderState.PAGADA);
		}*/
		state = orderRepository.getOrderState(stateId);

		OrdenPago order = orderRepository.get(numero);
		order.pagar(state, medioPago, idTransferencia, numeroChequera, numeroCheque,
				fechaCheque, observaciones, observacionesShopper);
		/*for (ItemOrden item : order.getItems()) {
			Debt debt = debtRepository.get(item.getAsignacion());
			if (debt != null) {
				debt.pagado();
			}
		}*/
		return order;
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

	public void setMedioPagoId(final long medioPagoId) {
		this.medioPagoId = medioPagoId;
	}

	public void setNumeroChequera(String numeroChequera) {
		this.numeroChequera = numeroChequera;
	}

	public void setNumeroCheque(String numeroCheque) {
		this.numeroCheque = numeroCheque;
	}

	public void setFechaCheque(Date fechaCheque) {
		this.fechaCheque = fechaCheque;
	}

	public void setIdTransferencia(String idTransferencia) {
		this.idTransferencia = idTransferencia;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public void setObservacionesShopper(String observacionesShopper) {
		this.observacionesShopper = observacionesShopper;
	}

	public void setStateId(final Integer stateId) {
		this.stateId = stateId;
	}
}
