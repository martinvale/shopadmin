package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="programas")
public class Programa {

  @Id
  @Column(name="id")
  @GeneratedValue
  private long id;

  @Column(name="programa")
  private String nombre;

  Programa() {
  }

  public long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }
}
