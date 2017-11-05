package com.ibiscus.shopnchek.application.order;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.MedioPago;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;

public class SavePaymentDataCommand implements Command<List<OrdenPago>> {

    private OrderRepository orderRepository;

    private Set<Long> numeros;

    private long medioPagoId;

    private String idTransferencia;

    private String observaciones;

    private String observacionesShopper;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<OrdenPago> execute() {
        List<OrdenPago> orders = new ArrayList<OrdenPago>();
        MedioPago medioPago = orderRepository.getMedioPago(medioPagoId);

        for (Long numero : numeros) {
            OrdenPago order = orderRepository.get(numero);
            order.updatePaymentData(medioPago, idTransferencia, observaciones,
                    observacionesShopper);
            orders.add(order);
        }
        return orders;
    }

    public void setOrderRepository(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void update(Set<Long> numeros, long medioPagoId, String idTransferencia,
            String observaciones, String observacionesShopper) {
        this.numeros = new HashSet<Long>(numeros);
        this.medioPagoId = medioPagoId;
        this.idTransferencia = idTransferencia;
        this.observaciones = observaciones;
        this.observacionesShopper = observacionesShopper;
    }

}
