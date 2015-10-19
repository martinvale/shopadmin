package com.ibiscus.shopnchek.domain.debt;

public enum TipoPago {
	honorarios("Honorarios"),
	reintegros("Reintegros"),
	otrosgastos("Otros gastos");

	private final String description;

	TipoPago(final String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
