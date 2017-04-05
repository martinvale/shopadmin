package com.ibiscus.shopnchek.application.order;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.ibiscus.shopnchek.domain.admin.OrdenPago;

public class OrderDto {

    private static final DateFormat dateFormat = new SimpleDateFormat(
            "dd/MM/yyyy");

    private static final DecimalFormat numberFormat = new DecimalFormat("#.00");

    private long numero;

    private String titular;

    private String importe = "0";

    private String fechaPago;

    private String state;

    private String cuit;

    private String banco;

    private String cbu;

    public OrderDto(final OrdenPago order, final String titular, final String cuit,
            final String banco, final String cbu, final double importe) {
        this(order, titular, cuit, banco, cbu);
        if (importe > 0) {
            this.importe = numberFormat.format(importe);
        }
    }

    public OrderDto(final OrdenPago order, final String titular, final String cuit,
            final String banco, final String cbu) {
        this.numero = order.getNumero();
        this.titular = titular;
        this.cuit = cuit;
        this.banco = banco;
        this.cbu = cbu;
        if (order.getImporte() > 0) {
            this.importe = numberFormat.format(order.getImporte());
        }
        this.fechaPago = dateFormat.format(order.getFechaPago());
        this.state = order.getEstado().getDescription();
    }

    public long getNumero() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    public String getImporte() {
        return importe;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public String getCuit() {
        return cuit;
    }

    public String getBanco() {
        return banco;
    }

    public String getCbu() {
        return cbu;
    }

    public String getState() {
        return state;
    }
}
