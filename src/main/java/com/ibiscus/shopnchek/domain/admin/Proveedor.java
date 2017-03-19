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

  @Column(name="descripcion")
  private String description;

  Proveedor() {
  }

  public Proveedor(final String unaDescripcion) {
    this.description = unaDescripcion;
  }

  public void update(final String unaDescripcion) {
    this.description = unaDescripcion;
  }

  public void updateId(final long theId) {
    id = theId;
  }

  public long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

}
