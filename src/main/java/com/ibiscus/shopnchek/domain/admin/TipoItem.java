package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tipos_item")
public class TipoItem {

  @Id
  @Column(name="id")
  private long id;

  @Column(name="descripcion")
  private String description;

  TipoItem() {
  }

  public long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }
}
