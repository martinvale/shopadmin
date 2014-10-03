package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ShopmetricsClientes")
public class ClienteShopmetrics {

  @Id
  @Column(name="ClientID")
  private int id;

  @Column(name="ClientName")
  private String nombre;

  ClienteShopmetrics() {
  }

  public int getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }
}
