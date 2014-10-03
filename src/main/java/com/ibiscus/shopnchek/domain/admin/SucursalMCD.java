package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sucursales")
public class SucursalMCD {

  @Id
  @Column(name="site_number")
  private long id;

  @Column(name="city")
  private String city;

  @Column(name="direccion1")
  private String address1;

  @Column(name="direccion2")
  private String address2;

  SucursalMCD() {
  }

  public long getId() {
    return id;
  }

  public String getCity() {
    return city;
  }

  public String getAddress1() {
    return address1;
  }

  public String getAddress2() {
    return address2;
  }

  public String getDescription() {
    String description = city + " - ";
    if (address1 != null) {
      description += address1;
    }
    if (address2 != null) {
      description += " " + address2;
    }
    return description;
  }
}
