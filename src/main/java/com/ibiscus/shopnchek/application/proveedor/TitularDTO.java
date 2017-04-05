package com.ibiscus.shopnchek.application.proveedor;

public class TitularDTO {

    public static final int SHOPPER = 1;

    public static final int PROVEEDOR = 2;

    private long titularId;

    private int titularTipo;

    private String name;

    private String email;

    private String loginShopmetrics;

    private String cuit;

    private String factura;

    private String banco;

    private String cbu;

    private String number;

    public TitularDTO(long titularId, int titularTipo, String name, String email,
            String loginShopmetrics, String cuit, String factura, String banco,
            String cbu, String number) {
        super();
        this.titularId = titularId;
        this.titularTipo = titularTipo;
        this.name = name;
        this.email = email;
        this.loginShopmetrics = loginShopmetrics;
        this.cuit = cuit;
        this.factura = factura;
        this.banco = banco;
        this.cbu = cbu;
        this.number = number;
    }

    public void update(long titularId, int titularTipo, String name,
            String loginShopmetrics, String cuit, String factura, String banco,
            String cbu, String number) {
        this.titularId = titularId;
        this.titularTipo = titularTipo;
        this.name = name;
        this.loginShopmetrics = loginShopmetrics;
        this.cuit = cuit;
        this.factura = factura;
        this.banco = banco;
        this.cbu = cbu;
        this.number = number;
    }

    public long getTitularId() {
        return titularId;
    }

    public int getTitularTipo() {
        return titularTipo;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLoginShopmetrics() {
        return loginShopmetrics;
    }

    public String getCuit() {
        return cuit;
    }

    public String getFactura() {
        return factura;
    }

    public String getBanco() {
        return banco;
    }

    public String getCbu() {
        return cbu;
    }

    public String getNumber() {
        return number;
    }

}
