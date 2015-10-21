package com.ibiscus.shopnchek.application.shopmetrics;

public class ShopperNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String identifier;

	public ShopperNotFoundException(final String identifier) {
		super("Cannot find shopper with identifier " + identifier);
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}
}
