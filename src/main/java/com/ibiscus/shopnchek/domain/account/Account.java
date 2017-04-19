package com.ibiscus.shopnchek.domain.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="account")
public class Account {

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "titular_id", nullable = false)
  private long titularId;

  @Column(name = "titular_tipo", nullable = false)
  private int titularTipo;

  @Column(name = "linked")
  private boolean linked;

  @Column(name = "cuit", length = 25)
  private String cuit;

  @Column(name = "factura", length = 1)
  private String factura;

  @Column(name = "banco", length = 200)
  private String banco;

  @Column(name = "cbu", length = 100)
  private String cbu;

  @Column(name = "number", length = 50)
  private String number;

  @Column(name = "billing_id")
  private Long billingId;

  @Column(name = "billing_tipo")
  private Integer billingTipo;

  Account() {
  }

  public Account(final long titularId, final int titularTipo, final String unCuit,
      final String unaFactura, final String banco, final String cbu, final String number,
      final boolean linked, final Long billingId, final Integer billingTipo) {
    this.titularId = titularId;
    this.titularTipo = titularTipo;
    this.cuit = unCuit;
    this.factura = unaFactura;
    this.banco = banco;
    this.cbu = cbu;
    this.number = number;
    this.linked = linked;
    this.billingId = billingId;
    this.billingTipo = billingTipo;
  }

  public void update(final String unCuit, final String unaFactura, final String banco,
      final String cbu, final String number, final boolean linked, final Long billingId,
      final Integer billingTipo) {
    this.cuit = unCuit;
    this.factura = unaFactura;
    this.banco = banco;
    this.cbu = cbu;
    this.number = number;
    this.linked = linked;
    this.billingId = billingId;
    this.billingTipo = billingTipo;
  }

  public long getId() {
    return id;
  }

  public boolean isLinked() {
      return linked;
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

  public long getTitularId() {
    return titularId;
  }

  public int getTitularTipo() {
    return titularTipo;
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

}
