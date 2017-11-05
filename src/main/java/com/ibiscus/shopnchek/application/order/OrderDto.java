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

    private String titularCuenta;

    private String importe = "0";

    private double iva;

    private String importeConIva = "0";

    private String fechaPago;

    private String state;

    private String cuit;

    private String banco;

    private String cbu;

    private String transferId;

    private String observaciones;

    private String observacionesShopper;

    public OrderDto(final OrdenPago order, final String titular, final String titularCuenta,
            final String cuit, final String banco, final String cbu, String transferId, final String observaciones,
            final String observacionesShopper, final double importe, final double honorarios, final double iva) {
        this(order, titular, titularCuenta, cuit, banco, cbu, transferId, observaciones, observacionesShopper);
        this.iva = iva;
        if (importe > 0) {
            this.importe = numberFormat.format(importe);
            double impuestos = honorarios * (iva / 100);
            this.importeConIva = numberFormat.format(importe + impuestos);
        }
    }

    public OrderDto(final OrdenPago order, final String titular, final String titularCuenta,
            final String cuit, final String banco, final String cbu, String transferId, final String observaciones,
            final String observacionesShopper) {
        this.numero = order.getNumero();
        this.titular = titular;
        this.titularCuenta = titularCuenta;
        this.cuit = cuit;
        this.banco = banco;
        this.cbu = cbu;
        this.iva = order.getIva();
        if (order.getImporte() > 0) {
            this.importe = numberFormat.format(order.getImporte());
            double impuestos = order.getHonorarios() * (order.getIva() / 100);
            this.importeConIva = numberFormat.format(order.getImporte() + impuestos);
        }
        this.fechaPago = dateFormat.format(order.getFechaPago());
        this.state = order.getEstado().getDescription();
        this.transferId = transferId;
        this.observaciones = observaciones;
        this.observacionesShopper = observacionesShopper;
    }

    public long getNumero() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    public String getTitularCuenta() {
        return titularCuenta;
    }

    public String getImporte() {
        return importe;
    }

    public String getImporteConIva() {
        return importeConIva;
    }

    public double getIva() {
        return iva;
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

    public String getTransferId() {
        return transferId;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public String getObservacionesShopper() {
        return observacionesShopper;
    }
}
