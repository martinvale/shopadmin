package com.ibiscus.shopnchek.domain.debt;

public enum TipoItem {
	iplan("iPlan"),
	mcd("MCD"),
	manual("Manual"),
	ingematica("Ingematica"),
	shopmetrics("Shopmetrics");

	private final String description;

	TipoItem(final String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
