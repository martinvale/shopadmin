package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="medios_pago")
public class MedioPago {

  @Id
  @Column(name="id")
  @GeneratedValue
  private long id;

  @Column(name="descripcion")
  private String description;

  MedioPago() {
  }

  public long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }
}
