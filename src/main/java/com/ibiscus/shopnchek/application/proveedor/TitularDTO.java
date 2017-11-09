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

    private boolean linked;

    private Long billingId;

    private Integer billingTipo;

    private String billingName;

    private String ultimaFactura;

    public TitularDTO(long titularId, int titularTipo, String name, String email,
            String loginShopmetrics, String cuit, String factura, String banco,
            String cbu, String number, boolean linked, Long billingId, Integer billingTipo,
            String billingName) {
        this(titularId, titularTipo, name, email, loginShopmetrics, cuit, factura,
                banco, cbu, number, linked, billingId, billingTipo, billingName, null);
    }

    public TitularDTO(long titularId, int titularTipo, String name, String email,
            String loginShopmetrics, String cuit, String factura, String banco,
            String cbu, String number, boolean linked, Long billingId, Integer billingTipo,
            String billingName, String ultimaFactura) {
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
        this.linked = linked;
        this.billingId = billingId;
        this.billingTipo = billingTipo;
        this.billingName = billingName;
        this.ultimaFactura = ultimaFactura;
    }

    public void update(long titularId, int titularTipo, String name,
            String loginShopmetrics, String cuit, String factura, String banco,
            String cbu, String number, boolean linked, Long billingId, Integer billingTipo,
            String billingName) {
        this.titularId = titularId;
        this.titularTipo = titularTipo;
        this.name = name;
        this.loginShopmetrics = loginShopmetrics;
        this.cuit = cuit;
        this.factura = factura;
        this.banco = banco;
        this.cbu = cbu;
        this.number = number;
        this.linked = linked;
        this.billingId = billingId;
        this.billingTipo = billingTipo;
        this.billingName = billingName;
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

    public Long getBillingId() {
        return billingId;
    }

    public Integer getBillingTipo() {
        return billingTipo;
    }

    public String getBillingName() {
        return billingName;
    }

    public boolean isLinked() {
        return linked;
    }

    public String getUltimaFactura() {
        return ultimaFactura;
    }

}
