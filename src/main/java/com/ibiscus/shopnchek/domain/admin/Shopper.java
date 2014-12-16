package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="shoppers")
public class Shopper {

  @Id
  @Column(name="id")
  @GeneratedValue
  private long id;

  @Column(name="nro_documento")
  private String dni;

  @Column(name="apellido_y_nombre")
  private String name;

  @Column(name="nombre")
  private String usuario;

  @Column(name="pais")
  private Integer pais;

  Shopper() {
  }

  public long getId() {
    return id;
  }

  public String getDni() {
    return dni;
  }

  public String getName() {
    return name;
  }

  public String getUsername() {
    return usuario;
  }

  public Integer getPais() {
    return pais;
  }
}
