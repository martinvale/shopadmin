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

  Account() {
  }

  public Account(final long titularId, final int titularTipo, final String unCuit,
      final String unaFactura, final String banco, final String cbu, final String number) {
    this.titularId = titularId;
    this.titularTipo = titularTipo;
    this.cuit = unCuit;
    this.factura = unaFactura;
    this.banco = banco;
    this.cbu = cbu;
    this.number = number;
  }

  public void update(final String unCuit, final String unaFactura, final String banco,
      final String cbu, final String number) {
    this.cuit = unCuit;
    this.factura = unaFactura;
    this.banco = banco;
    this.cbu = cbu;
    this.number = number;
  }

  public long getId() {
    return id;
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

}
