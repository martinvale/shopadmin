package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="estados_orden")
public class OrderState {

  public static final long ABIERTA = 1;
  public static final long VERIFICADA = 2;
  public static final long CERRADA = 3;
  public static final long PAGADA = 4;
  public static final long EN_ESPERA = 5;
  public static final long ANULADA = 6;

  @Id
  @Column(name="id")
  @GeneratedValue
  private long id;

  @Column(name="descripcion")
  private String description;

  OrderState() {
  }

  public long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }
}
