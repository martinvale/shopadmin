package com.ibiscus.shopnchek.domain.admin;


public class Adicional {

  private String shopperDni;

  private int id;

  private String pago;

  private String cliente;

  private String sucursal;

  private Integer mes;

  private Integer anio;

  private String fecha;

  private String observacion;

  private double importe;

  private int tipoPago;

  public Adicional(final String unShopperDni, final int theId,
      final String unPago, final String unCliente, final String unaSucursal,
      final int unMes, final int unAnio,
      final String unaFecha,
      final String unaObservacion,
      final double unImporte, final int unTipoPago) {
    shopperDni = unShopperDni;
    id = theId;
    pago = unPago;
    cliente = unCliente;
    sucursal = unaSucursal;
    mes = unMes;
    anio = unAnio;
    fecha = unaFecha;
    importe = unImporte;
    observacion = unaObservacion;
    tipoPago = unTipoPago;
  }

  /**
   * @return the shopperDni
   */
  public String getShopperDni() {
    return shopperDni;
  }

  public int getId() {
    return id;
  }

  /**
   * @return the programa
   */
  public String getPago() {
    return pago;
  }

  /**
   * @return the local
   */
  public String getCliente() {
    return cliente;
  }

  /**
   * @return the nombre
   */
  public String getSucursal() {
    return sucursal;
  }

  public String getObservacion() {
    return observacion;
  }

  /**
   * @return the mes
   */
  public Integer getMes() {
    return mes;
  }

  /**
   * @return the anio
   */
  public Integer getAnio() {
    return anio;
  }

  /**
   * @return the fecha
   */
  public String getFecha() {
    return fecha;
  }

  /**
   * @return the importe
   */
  public double getImporte() {
    return importe;
  }

  /**
   * @return the tipoPago
   */
  public int getTipoPago() {
    return tipoPago;
  }
}
