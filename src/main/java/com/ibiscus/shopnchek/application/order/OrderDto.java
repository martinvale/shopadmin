package com.ibiscus.shopnchek.application.order;

import java.util.Date;

import com.ibiscus.shopnchek.domain.admin.OrdenPago;

public class OrderDto {

	private long numero;

	private double importe;

	private Date fechaPago;

	private String state;

	public OrderDto(final OrdenPago order, final double importe) {
		this(order);
		this.importe = importe;
	}

	public OrderDto(final OrdenPago order) {
		this.numero = order.getNumero();
		this.importe = order.getImporte();
		this.fechaPago = order.getFechaPago();
		this.state = order.getEstado().getDescription();
	}

	public long getNumero() {
		return numero;
	}

	public double getImporte() {
		return importe;
	}

	public Date getFechaPago() {
		return fechaPago;
	}

	public String getState() {
		return state;
	}
}
