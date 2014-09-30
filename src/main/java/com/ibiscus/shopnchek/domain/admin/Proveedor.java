package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="proveedores")
public class Proveedor {

  @Id
  @Column(name="id")
  private long id;

  @Column(name="cuit")
  private String cuit;

  @Column(name="descripcion")
  private String description;

  @Column(name="factura")
  private String factura;

  Proveedor() {
  }

  public Proveedor(final String unCuit, final String unaDescripcion,
      final String unaFactura) {
    cuit = unCuit;
    description = unaDescripcion;
    factura = unaFactura;
  }

  public void update(final String unCuit, final String unaDescripcion,
      final String unaFactura) {
    cuit = unCuit;
    description = unaDescripcion;
    factura = unaFactura;
  }

  public void updateId(final long theId) {
    id = theId;
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
