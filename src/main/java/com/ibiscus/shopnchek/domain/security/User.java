package com.ibiscus.shopnchek.domain.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="USUARIOS")
public class User {

  @Id
  @Column(name="ID")
  private long id;

  @Column(name="DESCRIPCION")
  private String username;

  @Column(name="NOMBRE")
  private String name;

  @Column(name="PASSWORD")
  private String password;

  @Column(name="PERFIL")
  private int perfil;

  @Column(name="PERFIL_AUTORIZACION_ADICIONALES")
  private int autorizarAdicionales;

  User() {
  }

  public User(final String theUsername, final String theName,
      final int theProfile) {
    username = theUsername;
    name = theName;
    password = theUsername;
    perfil = theProfile;
  }

  public void update(final String theUsername, final String theName,
      final int theProfile) {
    username = theUsername;
    name = theName;
    perfil = theProfile;
  }

  public void updateId(final long theId) {
    id = theId;
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

  public int getPerfil() {
    return perfil;
  }

  public boolean isAditionalEnabled() {
    return autorizarAdicionales == 2;
  }
}
