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

  @Column(name="email", length=100)
  private String email;

  Proveedor() {
  }

  public Proveedor(final String unaDescripcion, final String email) {
    this.description = unaDescripcion;
    this.email = email;
  }

  public void update(final String unaDescripcion, final String email) {
    this.description = unaDescripcion;
    this.email = email;
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

  public String getEmail() {
    return email;
  }
}
