package com.ibiscus.shopnchek.application.order;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.application.email.CommunicationService;
import com.ibiscus.shopnchek.application.proveedor.TitularDTO;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.MedioPago;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.OrderState;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

public class PayOrderCommand implements Command<OrdenPago> {

    private static final DecimalFormat numberFormat = new DecimalFormat("#.00");

    private OrderRepository orderRepository;

    private ProveedorRepository proveedorRepository;

    private ShopperRepository shopperRepository;

    private CommunicationService communicationService;

    private String from;

    private long numero;

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

        OrdenPago order = orderRepository.get(numero);
        order.pagar(state, medioPago, idTransferencia, numeroChequera,
                numeroCheque, fechaCheque, observaciones, observacionesShopper);
        if (sendMail && !order.isNotified()) {
            Map<String, String> emailsToSend = getEmailsToSend(order);
            for (Entry<String, String> emailToSend : emailsToSend.entrySet()) {
                communicationService.sendMail(from, emailToSend.getKey(),
                        "Detalle de la orden de pago: " + order.getNumero(), emailToSend.getValue());
            }
            order.markAsNotified();
        }
        return order;
    }

    private Set<String> getEmailTitulares(final OrdenPago order) {
        Set<String> titulares = new HashSet<String>();
        if (order.getTipoProveedor().equals(TitularDTO.PROVEEDOR)) {
            Proveedor proveedor = proveedorRepository.get(order.getProveedor());
            if (!StringUtils.isBlank(proveedor.getEmail())) {
                titulares.add(proveedor.getEmail());
            }
        } else {
            Shopper shopper = shopperRepository.get(order.getProveedor());
            if (!StringUtils.isBlank(shopper.getEmail())) {
                titulares.add(shopper.getEmail());
            }
        }
        if (!StringUtils.isBlank(receivers)) {
            String[] extraReceivers = receivers.split(";");
            for (int i = 0; i < extraReceivers.length; i++) {
                titulares.add(extraReceivers[i]);
            }
        }
        return titulares;
    }

    private Set<String> getEmailShoppers(final OrdenPago order) {
        Set<String> emailShoppers = new HashSet<String>();
        for (ItemOrden item : order.getItems()) {
            Shopper shopper = shopperRepository.findByDni(item.getShopperDni());
            if (!StringUtils.isBlank(shopper.getEmail())) {
                emailShoppers.add(shopper.getEmail());
            }
        }
        return emailShoppers;
    }

    private Map<String, String> getEmailsToSend(final OrdenPago order) {
        Map<String, String> emailsToSend = new HashMap<String, String>();
        Set<String> emailTitulares = getEmailTitulares(order);
        for (String emailTitular : emailTitulares) {
            emailsToSend.put(emailTitular, getEmailText(order, emailTitular, true));
        }
        Set<String> emailShoppers = getEmailShoppers(order);
        for (String emailShopper : emailShoppers) {
            if (!emailTitulares.contains(emailShopper)) {
                emailsToSend.put(emailShopper, getEmailText(order, emailShopper, false));
            }
        }
        return emailsToSend;
    }

    private String getEmailText(final OrdenPago order, final String email, final boolean isTitular) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<body>");
        if (!StringUtils.isBlank(order.getObservacionesShopper())) {
            builder.append("<p>" + order.getObservacionesShopper() + "</p>");
        }
        builder.append("<p>Listado de items pagados:</p>");
        builder.append("<table style=\"text-align: center;\">");
        builder.append("<tr style=\"background-color: #FFF2CC; font-weight: bold;\">");
        builder.append("<td width=\"200px\">Shopper</td>");
        builder.append("<td width=\"100px\">Fecha</td>");
        builder.append("<td width=\"200px\">Cliente</td>");
        builder.append("<td width=\"200px\">Sucursal</td>");
        builder.append("<td width=\"100px\">Pago</td>");
        builder.append("<td width=\"100px\">Importe</td>");
        builder.append("</tr>");
        boolean even = false;
        double total = 0;
        for (ItemOrden item : order.getItems()) {
            Shopper shopper = shopperRepository.findByDni(item.getShopperDni());
            if (isTitular || (shopper.getEmail() != null && shopper.getEmail().equals(email))) {
                builder.append("<tr");
                if (even) {
                    builder.append(" style=\"background-color: #EDEDED;\"");
                }
                builder.append(">");
                builder.append("<td>" + shopper.getName() + "</td>");
                builder.append("<td>" + item.getFecha() + "</td>");
                builder.append("<td>" + item.getCliente() + "</td>");
                builder.append("<td>" + item.getSucursal() + "</td>");
                builder.append("<td>" + item.getTipoPago().getDescription() + "</td>");
                builder.append("<td>" + numberFormat.format(item.getImporte()) + "</td>");
                builder.append("</tr>");
                total = total + item.getImporte();
                even = !even;
            }
        }
        builder.append("<tr>");
        builder.append("<td>&nbsp;</td>");
        builder.append("<td>&nbsp;</td>");
        builder.append("<td>&nbsp;</td>");
        builder.append("<td>&nbsp;</td>");
        builder.append("<td>&nbsp;</td>");
        builder.append("<td style=\"background-color: #E2EFDA;\">Total: $ " + numberFormat.format(total) + "</td>");
        builder.append("</tr>");
        builder.append("</table>");
        builder.append("</body>");
        builder.append("</html>");
        return builder.toString();
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

    public void setStateId(final Long stateId) {
        this.stateId = stateId;
    }

    public void setSendMail(boolean sendMail) {
        this.sendMail = sendMail;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

}
