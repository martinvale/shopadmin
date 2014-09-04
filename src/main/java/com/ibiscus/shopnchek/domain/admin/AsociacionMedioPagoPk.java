package com.ibiscus.shopnchek.domain.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AsociacionMedioPagoPk implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  @Column(name="titular_tipo")
  private int titularTipo;

  @Column(name="titular_nro")
  private long titularNro;

  AsociacionMedioPagoPk() {
  }

  AsociacionMedioPagoPk(final int unTitularTipo, final long unTitularNro) {
    titularTipo = unTitularTipo;
    titularNro = unTitularNro;
  }

  /**
   * @return the titularTipo
   */
  public int getTitularTipo() {
    return titularTipo;
  }

  /**
   * @return the titularNro
   */
  public long getTitularNro() {
    return titularNro;
  }
}
