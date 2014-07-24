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

  @Column(name="nombre")
  private String name;

  Shopper() {
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
