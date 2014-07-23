package com.ibiscus.shopnchek.domain.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="USUARIOS")
public class User {

  @Id
  @Column(name="ID")
  @GeneratedValue
  private long id;

  @Column(name="DESCRIPCION")
  private String username;

  @Column(name="NOMBRE")
  private String name;

  @Column(name="PASSWORD")
  private String password;

  User() {
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }
}
