package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tipos_pago")
public class TipoPago {

  @Id
  @Column(name="id")
  @GeneratedValue
  private long id;

  @Column(name="descripcion")
  private String description;

  TipoPago() {
  }

  public long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }
}
