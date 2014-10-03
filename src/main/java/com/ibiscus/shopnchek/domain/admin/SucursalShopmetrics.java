package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="ShopmetricsSucursales")
public class SucursalShopmetrics {

  @Id
  @Column(name="StoreID")
  private String id;

  @Column(name="LocationName")
  private String location;

  @Column(name="City")
  private String city;

  @Column(name="Address")
  private String address;

  @ManyToOne
  @JoinColumn(name = "ClienteID", nullable = false)
  private ClienteShopmetrics client;

  SucursalShopmetrics() {
  }

  public String getId() {
    return id;
  }

  public String getLocation() {
    return location;
  }

  public String getCity() {
    return city;
  }

  public String getAddress() {
    return address;
  }

  public ClienteShopmetrics getClient() {
    return client;
  }

  public String getDescription() {
    String description = location + " - " + city;
    if (address != null) {
      description += ", " + address;
    }
    return description;
  }
}
