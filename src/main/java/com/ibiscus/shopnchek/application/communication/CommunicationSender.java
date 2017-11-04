package com.ibiscus.shopnchek.application.communication;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibiscus.shopnchek.application.email.CommunicationService;
import com.ibiscus.shopnchek.application.proveedor.TitularDTO;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.Proveedor;
import com.ibiscus.shopnchek.domain.admin.ProveedorRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

public class CommunicationSender implements Runnable {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(CommunicationSender.class);

    private static final DecimalFormat numberFormat = new DecimalFormat("#.00");

    private final CommunicationService communicationService;
    private final ShopperRepository shopperRepository;
    private final ProveedorRepository proveedorRepository;
    private final OrderRepository orderRepository;
    private final String from;
    private final String receivers;
    private final long orderNumber;

    public CommunicationSender(CommunicationService communicationService,
            ShopperRepository shopperRepository, ProveedorRepository proveedorRepository,
            OrderRepository orderRepository, String from, String receivers, long orderNumber) {
        this.communicationService = communicationService;
        this.shopperRepository = shopperRepository;
        this.proveedorRepository = proveedorRepository;
        this.orderRepository = orderRepository;
        this.from = from;
        this.receivers = receivers;
        this.orderNumber = orderNumber;
    }
    @Override
    public void run() {
        LOGGER.info("Sending communication for order number: {}", orderNumber);
        OrdenPago order = orderRepository.get(orderNumber);
        Map<String, String> emailsToSend = getEmailsToSend(order);
        for (Entry<String, String> emailToSend : emailsToSend.entrySet()) {
            communicationService.sendMail(from, emailToSend.getKey(),
                    "Detalle de la orden de pago: " + order.getNumero(), emailToSend.getValue());
        }
        order.markAsNotified();
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
        builder.append("<td width=\"100px\">Importe c/IVA</td>");
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
        double impuestos = total * (order.getIva() / 100);
        builder.append("<td style=\"background-color: #E2EFDA;\">Total c/IVA: $ " + numberFormat.format(total + impuestos) + "</td>");
        builder.append("</tr>");
        builder.append("</table>");
        builder.append("</body>");
        builder.append("</html>");
        return builder.toString();
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

}
