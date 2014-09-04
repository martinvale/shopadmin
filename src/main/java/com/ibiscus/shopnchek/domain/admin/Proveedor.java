package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="proveedores")
public class Proveedor {

  @Id
  @Column(name="id")
  @GeneratedValue
  private long id;

  @Column(name="cuit")
  private String cuit;

  @Column(name="descripcion")
  private String description;

  @Column(name="factura")
  private String factura;

  Proveedor() {
  }

  public long getId() {
    return id;
  }

  public String getCuit() {
    return cuit;
  }

  public String getDescription() {
    return description;
  }

  public String getFactura() {
    return factura;
  }
}
