package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="titulares_medios_pago")
public class AsociacionMedioPago {

  @Id
  private AsociacionMedioPagoPk asociacionMedioPagoPk;

  @Column(name="medio_pago")
  private long medioPago;

  AsociacionMedioPago() {
  }

  public AsociacionMedioPago(final int unTitularTipo, final long unTitularNro,
      final long unMedioPago) {
    asociacionMedioPagoPk = new AsociacionMedioPagoPk(unTitularTipo,
        unTitularNro);
    medioPago = unMedioPago;
  }

  public void update(final long unMedioPago) {
    medioPago = unMedioPago;
  }

  /**
   * @return the titularTipo
   */
  @Transient
  public int getTitularTipo() {
    return asociacionMedioPagoPk.getTitularTipo();
  }

  /**
   * @return the titularNro
   */
  @Transient
  public long getTitularNro() {
    return asociacionMedioPagoPk.getTitularNro();
  }

  /**
   * @return the medioPago
   */
  public long getMedioPago() {
    return medioPago;
  }
}
