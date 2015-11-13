package com.ibiscus.shopnchek.application.order;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.AsociacionMedioPago;
import com.ibiscus.shopnchek.domain.admin.MedioPago;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;

public class AsociarMedioPagoCommand implements Command<MedioPago> {

	private OrderRepository orderRepository;

	private long numeroOrden;

	private int medioPagoId;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public MedioPago execute() {
		OrdenPago order = orderRepository.get(numeroOrden);
		MedioPago medioPago = orderRepository.getMedioPago(medioPagoId);
		AsociacionMedioPago asociacion = orderRepository.findAsociacion(
				order.getTipoProveedor(), order.getProveedor());
		if (asociacion == null) {
			asociacion = new AsociacionMedioPago(order.getTipoProveedor(),
					order.getProveedor(), medioPagoId);
			orderRepository.save(asociacion);
		} else {
			asociacion.update(medioPagoId);
		}

		return medioPago;
	}

	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public void setNumeroOrden(final long numeroOrden) {
		this.numeroOrden = numeroOrden;
	}

	public void setMedioPagoId(final int medioPagoId) {
		this.medioPagoId = medioPagoId;
	}
}
