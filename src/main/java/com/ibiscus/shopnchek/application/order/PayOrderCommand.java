package com.ibiscus.shopnchek.application.order;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.application.communication.CommunicationSender;
import com.ibiscus.shopnchek.application.email.CommunicationService;
import com.ibiscus.shopnchek.domain.admin.MedioPago;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

public class PayOrderCommand implements Command<OrdenPago> {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private OrderRepository orderRepository;

    private ProveedorRepository proveedorRepository;

    private ShopperRepository shopperRepository;

    private CommunicationService communicationService;

    private String from;

    private Set<Long> numeros;

    private long medioPagoId;

    private String numeroChequera;

    private String numeroCheque;

    private Date fechaCheque;

    private String idTransferencia;

    private String observaciones;

    private String observacionesShopper;

    private Long stateId;

    private String receivers;

    private boolean sendMail;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public OrdenPago execute() {
        MedioPago medioPago = orderRepository.getMedioPago(medioPagoId);
        OrderState state = orderRepository.getOrderState(stateId);

        for (Long numero : numeros) {
            OrdenPago order = orderRepository.get(numero);
            order.pagar(state, medioPago, idTransferencia, numeroChequera,
                    numeroCheque, fechaCheque, observaciones, observacionesShopper);
            if (sendMail && !order.isNotified()) {
                executorService.submit(new CommunicationSender(communicationService,
                        shopperRepository, proveedorRepository,
                        orderRepository, from, receivers, order.getNumero()));
            }
        }
        return null;
    }

    public void setOrderRepository(final OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void setProveedorRepository(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public void setShopperRepository(ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }

    public void setCommunicationService(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void update(Set<Long> numeros, long medioPagoId, String numeroChequera,
            String numeroCheque, Date fechaCheque, String idTransferencia, String observaciones,
            String observacionesShopper, Long stateId, boolean sendMail, String receivers) {
        this.numeros = new HashSet<Long>(numeros);
        this.medioPagoId = medioPagoId;
        this.numeroChequera = numeroChequera;
        this.numeroCheque = numeroCheque;
        this.fechaCheque = fechaCheque;
        this.idTransferencia = idTransferencia;
        this.observaciones = observaciones;
        this.observacionesShopper = observacionesShopper;
        this.stateId = stateId;
        this.sendMail = sendMail;
        this.receivers = receivers;
    }

}
