package com.ibiscus.shopnchek.domain.report;

import java.math.BigInteger;

public class Key {
    private final int year;
    private final int month;
    private final BigInteger clientId;
    private final String client;
    private final String shopperDni;
    private final String shopper;

    public Key(int year, int month, BigInteger clientId, String client, String shopperDni, String shopper) {
        this.year = year;
        this.month = month;
        this.clientId = clientId;
        this.client = client;
        this.shopperDni = shopperDni;
        this.shopper = shopper;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public BigInteger getClientId() {
        return clientId;
    }

    public String getClient() {
        return client;
    }

    public String getShopperDni() {
        return shopperDni;
    }

    public String getShopper() {
        return shopper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Key key = (Key) o;

        if (year != key.year) return false;
        if (month != key.month) return false;
        if (clientId != null ? !clientId.equals(key.clientId) : key.clientId != null) return false;
        if (client != null ? !client.equals(key.client) : key.client != null) return false;
        if (shopperDni != null ? !shopperDni.equals(key.shopperDni) : key.shopperDni != null) return false;
        return shopper != null ? shopper.equals(key.shopper) : key.shopper == null;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (shopperDni != null ? shopperDni.hashCode() : 0);
        result = 31 * result + (shopper != null ? shopper.hashCode() : 0);
        return result;
    }
}
